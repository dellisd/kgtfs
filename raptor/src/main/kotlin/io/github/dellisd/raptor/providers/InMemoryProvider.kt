@file:OptIn(ExperimentalTurfApi::class)

package io.github.dellisd.raptor.providers

import com.github.davidmoten.rtree2.RTree
import com.github.davidmoten.rtree2.geometry.Geometries
import com.github.davidmoten.rtree2.internal.EntryDefault
import io.github.dellisd.kgtfs.domain.model.RouteId
import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.TripId
import io.github.dellisd.kgtfs.dsl.gtfs
import io.github.dellisd.raptor.RaptorDataProvider
import io.github.dellisd.raptor.models.GtfsTime
import io.github.dellisd.raptor.models.StopTime
import io.github.dellisd.raptor.models.Transfer
import io.github.dellisd.raptor.utils.uniqueTripSequences
import io.github.dellisd.spatialk.geojson.dsl.lngLat
import io.github.dellisd.spatialk.turf.ExperimentalTurfApi
import io.github.dellisd.spatialk.turf.Units
import io.github.dellisd.spatialk.turf.convertLength
import io.github.dellisd.spatialk.turf.distance
import org.slf4j.LoggerFactory

class InMemoryProvider(source: String) : RaptorDataProvider {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val routesAtStop: MutableMap<StopId, MutableSet<RouteId>> = mutableMapOf()
    private val stopsAlongRoute: MutableMap<RouteId, List<StopId>> = mutableMapOf()

    private val stopTimesForTrip: MutableMap<TripId, List<StopTime>> = mutableMapOf()
    private val tripsForRoute: MutableMap<RouteId, Set<TripId>> = mutableMapOf()
    private val transfers: MutableMap<StopId, Set<Transfer>> = mutableMapOf()

    init {
        logger.info("Initializing InMemoryProvider")
        gtfs(source, dbPath = "gtfs.db") {
            // TODO: Update day as needed
            val today = calendar.today().map { it.serviceId }.toSet()
            logger.info("Today's calendars: $today")

            logger.info("Computing unique trip sequences")
            val allTimes = stopTimes.getAll()
            val sequences = uniqueTripSequences(
                allTimes,
                trips.getAll()
                    .associateBy { it.id }
                    .filter { (_, trip) -> trip.serviceId in today }) // Only trips from today

            logger.info("Loading stop and route info")
            // Get all stop times grouped by trip
            allTimes.groupBy { it.tripId }
                .mapValuesTo(stopTimesForTrip) { (_, times) ->
                    times.mapIndexed { index, time ->
                        StopTime(time.stopId, GtfsTime(time.arrivalTime), index)
                    }
                }

            // Deal with stop sequences
            sequences.forEach { (route, sequence, trips) ->
                trips.forEach { (_, stops) ->
                    stops.forEach { time ->
                        routesAtStop.getOrPut(time.stopId, ::mutableSetOf).add(route)
                    }
                }
                stopsAlongRoute[route] = sequence
                tripsForRoute[route] = trips.keys
            }

            logger.info("Computing footpath estimates")
            val allStops = stops.getAll()
            // Compute estimates of footpath transfers
            val tree = RTree.create(
                allStops
                    .map { EntryDefault.entry(it.id, Geometries.pointGeographic(it.longitude!!, it.latitude!!)) })

            allStops.associateTo(transfers) { stop ->
                val results = tree.search(
                    Geometries.circle(
                        stop.longitude!!,
                        stop.latitude!!,
                        convertLength(500.0, to = Units.Degrees)
                    )
                )
                val stopPosition = lngLat(stop.longitude!!, stop.latitude!!)
                stop.id to results.asSequence().filter { it.value() != stop.id }.map { entry ->
                    val point = lngLat(entry.geometry().x(), entry.geometry().y())
                    Transfer(stop.id, entry.value(), distance(stopPosition, point, Units.Meters))
                }.toSet()
            }
        }
    }

    override fun getRoutesAtStop(stop: StopId): Set<RouteId> = routesAtStop[stop] ?: emptySet()

    override fun getStopsAlongRoute(route: RouteId): List<StopId> = stopsAlongRoute.getValue(route)

    override fun getStopTimes(trip: TripId): List<StopTime> = stopTimesForTrip.getValue(trip)

    override fun getEarliestTripAtStop(route: RouteId, index: Int, after: GtfsTime): TripId? {
        return tripsForRoute.getValue(route)
            .map { trip -> trip to stopTimesForTrip.getValue(trip)[index] }
            .sortedBy { (_, stop) -> stop.arrivalTime }
            .firstOrNull { (_, stop) -> stop.arrivalTime > after }?.first
    }

    override fun getTransfersAtStop(stop: StopId): Set<Transfer> = transfers.getValue(stop)

    override fun isStopBefore(route: RouteId, a: StopId, b: StopId): Boolean {
        getStopsAlongRoute(route).forEach {
            if (it == a) {
                return true
            } else if (it == b) {
                return false
            }
        }
        return false
    }
}
