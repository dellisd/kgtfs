package ca.derekellis.kgtfs.raptor.providers

import com.github.davidmoten.rtree2.RTree
import com.github.davidmoten.rtree2.geometry.Geometries
import com.github.davidmoten.rtree2.internal.EntryDefault
import ca.derekellis.kgtfs.domain.model.RouteId
import ca.derekellis.kgtfs.domain.model.StopId
import ca.derekellis.kgtfs.domain.model.TripId
import ca.derekellis.kgtfs.dsl.gtfs
import ca.derekellis.kgtfs.raptor.RaptorDataProvider
import ca.derekellis.kgtfs.raptor.db.executeAsSet
import ca.derekellis.kgtfs.raptor.db.getDatabase
import ca.derekellis.kgtfs.raptor.models.GtfsTime
import ca.derekellis.kgtfs.raptor.models.StopTime
import ca.derekellis.kgtfs.raptor.models.Transfer
import ca.derekellis.kgtfs.raptor.utils.uniqueTripSequences
import io.github.dellisd.spatialk.geojson.dsl.lngLat
import io.github.dellisd.spatialk.turf.Units
import io.github.dellisd.spatialk.turf.convertLength
import io.github.dellisd.spatialk.turf.distance
import org.slf4j.LoggerFactory

public class SqliteProvider(dbPath: String) : RaptorDataProvider {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val routesAtStop: MutableMap<StopId, Set<RouteId>> = mutableMapOf()

    private val database = getDatabase(dbPath)

    init {
        database.routeAtStopQueries.getAll().executeAsList().groupBy { it.stop_id }.forEach { (stop, list) ->
            routesAtStop[stop] = list.map { it.route_id }.toSet()
        }
    }

    public fun build(source: String): Unit = gtfs(source, dbPath = "") {
        // TODO: Handle different days
        val today = calendar.today().map { it.serviceId }.toSet()
        logger.info("Using calendars: $today")

        logger.info("Computing unique trip sequences")
        val allTimes = stopTimes.getAll()
        val sequences = uniqueTripSequences(
            allTimes,
            trips.getAll()
                .associateBy { it.id }
                .filter { (_, trip) -> trip.serviceId in today }) // Only trips from today

        logger.info("Loading stop and route info")
        database.transaction {
            stops.getAll().forEach { database.stopQueries.insert(it.id) }

            sequences.forEach {
                database.routeQueries.insert(it.route)
                it.sequence.forEachIndexed { index, stop ->
                    database.routeAtStopQueries.insert(stop, it.route, index + 1)
                }

                it.trips.forEach { (trip, times) ->
                    database.tripQueries.insert(trip, it.route)
                    times.forEachIndexed { index, time ->
                        database.stopTimeQueries.insert(trip, time.stopId, GtfsTime(time.arrivalTime), index + 1)
                    }
                }
            }
        }

        logger.info("Computing footpath estimates")
        val allStops = stops.getAll()
        // Compute estimates of footpath transfers
        val tree = RTree.create(
            allStops
                .map { EntryDefault.entry(it.id, Geometries.pointGeographic(it.longitude!!, it.latitude!!)) })

        val allTransfers = allStops.flatMap { stop ->
            val results = tree.search(
                Geometries.circle(
                    stop.longitude!!,
                    stop.latitude!!,
                    convertLength(500.0, to = Units.Degrees)
                )
            )
            val stopPosition = lngLat(stop.longitude!!, stop.latitude!!)
            results.asSequence().filter { it.value() != stop.id }.map { entry ->
                val point = lngLat(entry.geometry().x(), entry.geometry().y())
                Transfer(stop.id, entry.value(), distance(stopPosition, point, Units.Meters), null)
            }
        }

        database.transaction {
            allTransfers.forEach { (from, to, distance) ->
                database.transferQueries.insert(from, to, distance, null)
            }
        }
    }

    override fun getRoutesAtStop(stop: StopId): Set<RouteId> = routesAtStop[stop] ?: emptySet()

    override fun getStopsAlongRoute(route: RouteId): List<StopId> =
        database.routeAtStopQueries.getByRoute(route) { stop, _, _ -> stop }.executeAsList()

    override fun getStopTimes(trip: TripId): List<StopTime> {
        return database.stopTimeQueries.getByTrip(trip) { _, stop, arrival, sequence ->
            StopTime(stop, arrival, sequence)
        }.executeAsList()
    }

    override fun getEarliestTripAtStop(route: RouteId, index: Int, after: GtfsTime): TripId? =
        database.stopTimeQueries.getEarliest(route, after, index) { trip, _, _, _ -> trip }.executeAsOneOrNull()

    override fun getTransfersAtStop(stop: StopId): Set<Transfer> =
        database.transferQueries.getByOrigin(stop) { origin, destination, distance, geometry ->
            Transfer(origin, destination, distance, geometry)
        }.executeAsSet()

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
