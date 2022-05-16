package ca.derekellis.kgtfs.raptor

import ca.derekellis.kgtfs.domain.model.GtfsTime
import ca.derekellis.kgtfs.domain.model.RouteId
import ca.derekellis.kgtfs.domain.model.StopId
import ca.derekellis.kgtfs.domain.model.TripId
import ca.derekellis.kgtfs.domain.model.toGtfsTime
import ca.derekellis.kgtfs.raptor.models.Journey
import ca.derekellis.kgtfs.raptor.models.Leg
import ca.derekellis.kgtfs.raptor.models.RouteLeg
import ca.derekellis.kgtfs.raptor.models.StopTime
import ca.derekellis.kgtfs.raptor.models.Transfer
import ca.derekellis.kgtfs.raptor.models.TransferLeg
import ca.derekellis.kgtfs.raptor.utils.takeLastWhileInclusive
import java.time.Duration
import java.time.LocalDateTime

public class Raptor(private val provider: RaptorDataProvider, public val walkingSpeed: Double = 1.4) {
    public fun journeys(origin: StopId, destination: StopId, time: LocalDateTime): List<Journey> {
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
                var trip: TripId? = null
                var stopTimes: List<StopTime>? = null
                stopsAlongRoute.forEachIndexed { i, stopAlongRoute ->
                    if (stopTimes != null && stopTimes!![i + indexOfStop].arrivalTime < labels[k]
                            .getOrDefault(stopAlongRoute, GtfsTime.MAX)
                    ) {
                        labels[k][stopAlongRoute] = stopTimes!![i + indexOfStop].arrivalTime
                        // Record this leg of the trip
                        connections.getOrPut(stopAlongRoute, ::mutableMapOf)[k] =
                            RouteLeg(boardingStop!!, stopAlongRoute, trip!!)

                        toBeMarked.add(stopAlongRoute)
                    }

                    if (stopTimes == null || labels[k - 1].getOrDefault(
                            stopAlongRoute,
                            GtfsTime.MAX
                        ) < stopTimes!![i].arrivalTime
                    ) {
                        trip = provider.getEarliestTripAtStop(
                            route,
                            i + indexOfStop,
                            labels[k - 1].getOrDefault(stopAlongRoute, GtfsTime.MAX)
                        )
                        stopTimes = trip?.let { provider.getStopTimes(it) }

                        if (trip != null) {
                            boardingStop = stopAlongRoute
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
                                transfer.duration,
                                transfer.distance,
                                transfer.geometry
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
        destination: StopId
    ): List<Journey> {
        return connections[destination]?.keys?.mapNotNull outer@ { key ->
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
