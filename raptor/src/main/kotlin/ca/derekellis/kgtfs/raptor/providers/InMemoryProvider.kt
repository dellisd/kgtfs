@file:OptIn(ExperimentalTurfApi::class)

package ca.derekellis.kgtfs.raptor.providers

import ca.derekellis.kgtfs.domain.model.GtfsTime
import com.github.davidmoten.rtree2.RTree
import com.github.davidmoten.rtree2.geometry.Geometries
import com.github.davidmoten.rtree2.internal.EntryDefault
import ca.derekellis.kgtfs.domain.model.RouteId
import ca.derekellis.kgtfs.domain.model.StopId
import ca.derekellis.kgtfs.domain.model.TripId
import ca.derekellis.kgtfs.dsl.gtfs
import ca.derekellis.kgtfs.ext.uniqueTripSequences
import ca.derekellis.kgtfs.raptor.RaptorDataProvider
import ca.derekellis.kgtfs.raptor.db.executeAsSet
import ca.derekellis.kgtfs.raptor.db.getDatabase
import ca.derekellis.kgtfs.raptor.models.StopTime
import ca.derekellis.kgtfs.raptor.models.Transfer
import io.github.dellisd.spatialk.geojson.dsl.lngLat
import io.github.dellisd.spatialk.turf.ExperimentalTurfApi
import io.github.dellisd.spatialk.turf.Units
import io.github.dellisd.spatialk.turf.convertLength
import io.github.dellisd.spatialk.turf.distance
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.LocalDate

public class InMemoryProvider(
    source: String,
    cache: String = "",
    private val date: LocalDate = LocalDate.now()
) :
    RaptorDataProvider {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val routesAtStop: MutableMap<StopId, MutableSet<RouteId>> = mutableMapOf()
    private val stopsAlongRoute: MutableMap<RouteId, List<StopId>> = mutableMapOf()

    private val stopTimesForTrip: MutableMap<TripId, List<StopTime>> = mutableMapOf()
    private val tripsForRoute: MutableMap<RouteId, Set<TripId>> = mutableMapOf()
    private val transfers: MutableMap<StopId, Set<Transfer>> = mutableMapOf()

    init {
        logger.info("Initializing InMemoryProvider")

        if (cache.isNotEmpty()) {
            loadIndicesFromCache(cache)
        } else {
            // TODO: Delegate the suspension to somewhere else
            runBlocking {
                buildIndicesFromSource(source)
            }
        }
    }

    private suspend fun buildIndicesFromSource(source: String) = gtfs(source, dbPath = "gtfs.db") {
        logger.info("Building indices from $source")
        // TODO: Update day as needed
        val today = calendar.onDate(date).map { it.serviceId }.toSet()
        logger.debug("Today's calendars: $today")

        logger.debug("Computing unique trip sequences")
        val allTimes = stopTimes.getAll()
        val sequences = uniqueTripSequences(today)

        logger.debug("Loading stop and route info")
        // Get all stop times grouped by trip
        allTimes.groupBy { it.tripId }
            .mapValuesTo(stopTimesForTrip) { (_, times) ->
                times.mapIndexed { index, time ->
                    StopTime(time.stopId, time.arrivalTime, index)
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

        logger.debug("Computing footpath estimates")
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
                Transfer(stop.id, entry.value(), distance(stopPosition, point, Units.Meters), null)
            }.toSet()
        }
    }

    private fun loadIndicesFromCache(cache: String) {
        logger.info("Loading indices from $cache")
        val database = getDatabase(cache)

        logger.debug("Loading routes at stops and transfers at stop")
        database.stopQueries.getAll().executeAsList().forEach { stop ->
            val set = database.routeAtStopQueries.getByStop(stop) { _, route, _ -> route }.executeAsSet()
            routesAtStop[stop] = set.toMutableSet()

            val transfersAtStop = database.transferQueries.getByOrigin(stop, ::Transfer).executeAsSet()
            transfers[stop] = transfersAtStop
        }

        logger.debug("Loading stops along route and trips for route")
        database.routeQueries.getAll().executeAsList().forEach { route ->
            val stops = database.routeAtStopQueries.getByRoute(route) { stop, _, _ -> stop }.executeAsList()
            stopsAlongRoute[route] = stops

            val trips = database.tripQueries.getByRoute(route) { trip, _ -> trip }.executeAsSet()
            tripsForRoute[route] = trips
            trips.forEach { trip ->
                val stopTimes = database.stopTimeQueries.getByTrip(trip) { _, stop, arrival, sequence ->
                    StopTime(stop, arrival, sequence)
                }.executeAsList()

                stopTimesForTrip[trip] = stopTimes
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
