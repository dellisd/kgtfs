package ca.derekellis.kgtfs.raptor

import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId
import ca.derekellis.kgtfs.csv.toGtfsTime
import ca.derekellis.kgtfs.raptor.models.Journey
import ca.derekellis.kgtfs.raptor.models.Leg
import ca.derekellis.kgtfs.raptor.models.RouteLeg
import ca.derekellis.kgtfs.raptor.models.StopTime
import ca.derekellis.kgtfs.raptor.models.Transfer
import ca.derekellis.kgtfs.raptor.models.TransferLeg
import ca.derekellis.kgtfs.raptor.utils.takeLastWhileInclusive
import java.time.Duration
import java.time.LocalTime

public class Raptor(private val provider: RaptorDataProvider, public val walkingSpeed: Double = 1.4) {
  /**
   * Get a list of journeys between the [origin] and [destination] when departing at [time].
   * The journeys are returned in order of least-transfers to most-transfers and longest-duration to shortest-duration.
   *
   * @param buffer The amount of time to allocate for making transfers (a buffer between arriving at a stop and boarding a bus)
   */
  public fun journeys(
    origin: StopId,
    destination: StopId,
    time: LocalTime,
    buffer: Duration = Duration.ZERO,
  ): List<Journey> {
    // Earliest arrival times for a stop in the k-th round
    val labels = mutableListOf<MutableMap<StopId, GtfsTime>>()
    labels.add(mutableMapOf(origin to time.toGtfsTime()))

    // Connections being made in each route for every stop
    val connections = mutableMapOf<StopId, MutableMap<Int, Leg>>()

    val markedStops = mutableSetOf(origin)

    var k = 1
    while (markedStops.isNotEmpty()) {
      // New map of labels at index k
      labels.add(HashMap(labels[k - 1]))

      val queue = makeQueue(markedStops)
      val toBeMarked = mutableSetOf<StopId>()

      queue.forEach { (route, stop) ->
        // Get stops along the current route, but only look at stops *after* the current stop
        val allStopsAlongRoute = provider.getStopsAlongRoute(route)
        val indexOfStop = allStopsAlongRoute.indexOf(stop)
        val stopsAlongRoute = allStopsAlongRoute.takeLastWhileInclusive { it != stop }

        var boardingStop: StopId? = null
        var boardingIndex: Int = -1
        var trip: TripId? = null
        var stopTimes: List<StopTime>? = null
        stopsAlongRoute.forEachIndexed { i, stopAlongRoute ->
          if (stopTimes != null && stopTimes!![i + indexOfStop].arrivalTime < labels[k]
              .getOrDefault(stopAlongRoute, GtfsTime.MAX)
          ) {
            labels[k][stopAlongRoute] = stopTimes!![i + indexOfStop].arrivalTime
            // Record this leg of the trip
            connections.getOrPut(stopAlongRoute, ::mutableMapOf)[k] =
              RouteLeg(
                boardingStop!!,
                stopAlongRoute,
                stopTimes!![boardingIndex].arrivalTime,
                labels[k].getValue(stopAlongRoute),
                trip!!,
              )

            toBeMarked.add(stopAlongRoute)
          }

          if (stopTimes == null || labels[k - 1].getOrDefault(
              stopAlongRoute,
              GtfsTime.MAX,
            ) < stopTimes!![i].arrivalTime
          ) {
            trip = provider.getEarliestTripAtStop(
              route,
              i + indexOfStop,
              labels[k - 1].getOrDefault(stopAlongRoute, GtfsTime.MAX) + buffer,
            )
            stopTimes = trip?.let { provider.getStopTimes(it) }

            if (trip != null) {
              boardingStop = stopAlongRoute
              boardingIndex = i + indexOfStop
            }
          }
        }
      }

      markedStops.forEach { stop ->
        provider.getTransfersAtStop(stop).forEach { transfer ->
          val arrivalTime = labels[k - 1].getValue(stop) + transfer.duration

          if (arrivalTime < labels[k].getOrDefault(transfer.to, GtfsTime.MAX)) {
            labels[k][transfer.to] = arrivalTime
            // Record this leg of the trip
            connections.getOrPut(transfer.to, ::mutableMapOf)[k] =
              TransferLeg(
                transfer.from,
                transfer.to,
                start = labels[k - 1].getValue(stop),
                end = arrivalTime,
                transfer.duration,
                transfer.distance,
                transfer.geometry,
              )

            toBeMarked.add(transfer.to)
          }
        }
      }

      markedStops.clear()
      markedStops.addAll(toBeMarked)

      ++k
    }

    return connectionsToJourneys(connections, destination)
  }

  private fun makeQueue(marked: Set<StopId>): Map<RouteId, StopId> {
    val queue = mutableMapOf<RouteId, StopId>()

    marked.forEach { stop ->
      provider.getRoutesAtStop(stop).forEach { route ->
        if (!queue.containsKey(route) || provider.isStopBefore(route, stop, queue.getValue(route))) {
          queue[route] = stop
        }
      }
    }

    return queue
  }

  /**
   * Converts a transfer distance to a duration using the configured walking speed
   */
  private val Transfer.duration: Duration get() = Duration.ofSeconds((distance / walkingSpeed).toLong())

  private fun connectionsToJourneys(
    connections: Map<StopId, Map<Int, Leg>>,
    destination: StopId,
  ): List<Journey> {
    return connections[destination]?.keys?.mapNotNull outer@{ key ->
      var step = destination

      val legs = (key downTo 1).mapNotNull { k ->
        // Skip this plan if no path is possible
        // TODO: Review this?
        val leg = connections.getValue(step)[k] ?: return@outer null
        leg.also { step = leg.from }
      }

      return@outer Journey(legs.reversed())
    } ?: emptyList()
  }
}
