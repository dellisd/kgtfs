@file:OptIn(ExperimentalTurfApi::class)

package ca.derekellis.kgtfs.raptor.providers

import ca.derekellis.kgtfs.cache.GtfsCache
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId
import ca.derekellis.kgtfs.ext.uniqueTripSequences
import ca.derekellis.kgtfs.raptor.RaptorDataProvider
import ca.derekellis.kgtfs.raptor.db.getDatabase
import ca.derekellis.kgtfs.raptor.models.StopTime
import ca.derekellis.kgtfs.raptor.models.Transfer
import com.github.davidmoten.rtree2.RTree
import com.github.davidmoten.rtree2.geometry.Geometries
import com.github.davidmoten.rtree2.internal.EntryDefault
import io.github.dellisd.spatialk.geojson.dsl.lngLat
import io.github.dellisd.spatialk.turf.ExperimentalTurfApi
import io.github.dellisd.spatialk.turf.Units
import io.github.dellisd.spatialk.turf.convertLength
import io.github.dellisd.spatialk.turf.distance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate

public class InMemoryProvider private constructor(
    private val date: LocalDate = LocalDate.now()
) : RaptorDataProvider {
    private val routesAtStop: MutableMap<StopId, MutableSet<RouteId>> = mutableMapOf()
    private val stopsAlongRoute: MutableMap<RouteId, List<StopId>> = mutableMapOf()

    private val stopTimesForTrip: MutableMap<TripId, List<StopTime>> = mutableMapOf()
    private val tripsForRoute: MutableMap<RouteId, Set<TripId>> = mutableMapOf()
    private val transfers: MutableMap<StopId, Set<Transfer>> = mutableMapOf()

    private fun buildIndicesFromGtfs(source: GtfsCache) = source.read {
        // TODO: Update day as needed
        val today = calendars.onDate(date).map { it.serviceId }.toSet()
        val allTimes = stopTimes.all()
        val sequences = uniqueTripSequences(today)

        // Get all stop times grouped by trip
        allTimes.groupBy { it.tripId }
            .mapValuesTo(stopTimesForTrip) { (_, times) ->
                times.mapIndexed { index, time ->
                    StopTime(time.stopId, time.arrivalTime, index)
                }
            }

        // Deal with stop sequences
        sequences.forEach { (route, _, sequence, trips) ->
            trips.forEach { (_, stops) ->
                stops.forEach { time ->
                    routesAtStop.getOrPut(time.stopId, ::mutableSetOf).add(route)
                }
            }
            stopsAlongRoute[route] = sequence
            tripsForRoute[route] = trips.keys
        }

        val allStops = stops.all()
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
                Transfer(stop.id, entry.value(), distance(stopPosition, point, Units.Meters), null)
            }.toSet()
        }
    }

    private suspend fun loadIndicesFromCache(cache: String) = coroutineScope {
        val database = getDatabase(cache, readOnly = true)

        val stops = async {
            database.routeAtStopQueries.getAll().executeAsList()
                .groupBy { it.stop_id }
                .forEach { (key, value) -> routesAtStop[key] = value.map { it.route_id }.toMutableSet() }

            database.transferQueries.getAll(::Transfer).executeAsList()
                .groupBy { it.from }
                .forEach { (key, value) -> transfers[key] = value.toSet() }
        }

        val routes = async {
            database.routeAtStopQueries.getAll().executeAsList()
                .groupBy { it.route_id }
                .forEach { (key, value) -> stopsAlongRoute[key] = value.map { it.stop_id } }

            database.stopTimeQueries.getAll { trip, stop, arrival, sequence ->
                trip to StopTime(
                    stop,
                    arrival,
                    sequence
                )
            }.executeAsList()
                .groupBy { (trip) -> trip }
                .forEach { (key, value) -> stopTimesForTrip[key] = value.map { (_, time) -> time } }

            database.tripQueries.getAll().executeAsList()
                .groupBy { it.route_id }
                .forEach { (key, value) -> tripsForRoute[key] = value.map { it.id }.toSet() }
        }

        listOf(stops, routes).awaitAll()
    }

    override fun getRoutesAtStop(stop: StopId): Set<RouteId> = routesAtStop[stop] ?: emptySet()

    override fun getStopsAlongRoute(route: RouteId): List<StopId> = stopsAlongRoute.getValue(route)

    override fun getStopTimes(trip: TripId): List<StopTime> = stopTimesForTrip.getValue(trip)

    override fun getEarliestTripAtStop(route: RouteId, index: Int, after: GtfsTime): TripId? {
        return tripsForRoute.getValue(route)
            .asSequence()
            .map { trip -> trip to stopTimesForTrip.getValue(trip)[index] }
            .sortedBy { (_, stop) -> stop.arrivalTime }
            .firstOrNull { (_, stop) -> stop.arrivalTime > after }?.first
    }

    override fun getTransfersAtStop(stop: StopId): Set<Transfer> = transfers[stop] ?: emptySet()

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

    public companion object {
        public suspend fun fromCache(cacheFile: String, date: LocalDate = LocalDate.now()): InMemoryProvider {
            return InMemoryProvider(date).apply {
                loadIndicesFromCache(cacheFile)
            }
        }

        public fun fromGtfs(source: GtfsCache, date: LocalDate = LocalDate.now()): InMemoryProvider {
            return InMemoryProvider(date).apply {
                buildIndicesFromGtfs(source)
            }
        }
    }
}
