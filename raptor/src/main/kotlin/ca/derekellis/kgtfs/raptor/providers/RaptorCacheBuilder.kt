package ca.derekellis.kgtfs.raptor.providers

import com.github.davidmoten.rtree2.RTree
import com.github.davidmoten.rtree2.geometry.Geometries
import com.github.davidmoten.rtree2.internal.EntryDefault
import ca.derekellis.kgtfs.domain.model.Stop
import ca.derekellis.kgtfs.dsl.gtfs
import ca.derekellis.kgtfs.raptor.db.getDatabase
import ca.derekellis.kgtfs.raptor.models.GtfsTime
import ca.derekellis.kgtfs.raptor.models.Transfer
import ca.derekellis.kgtfs.raptor.utils.uniqueTripSequences
import io.github.dellisd.spatialk.geojson.Position
import io.github.dellisd.spatialk.geojson.dsl.feature
import io.github.dellisd.spatialk.geojson.dsl.lineString
import io.github.dellisd.spatialk.geojson.dsl.lngLat
import io.github.dellisd.spatialk.turf.Units
import io.github.dellisd.spatialk.turf.convertLength
import io.github.dellisd.spatialk.turf.distance
import org.slf4j.LoggerFactory

public val DefaultTransferMapper: (Stop, List<Stop>) -> List<Transfer> = { origin: Stop, destinations: List<Stop> ->
    destinations.map { dest ->
        Transfer(
            origin.id, dest.id,
            distance(origin.position, dest.position, Units.Meters),
            feature(geometry = lineString {
                +origin.position
                +dest.position
            })
        )
    }
}

private val Stop.position: Position
    get() = lngLat(longitude!!, latitude!!)

@DslMarker
public annotation class RaptorCacheDsl

/**
 * Build a cache that can be efficiently loaded into the [InMemoryProvider] for use in RAPTOR.
 * The cache is saved on disk as a SQLite database.
 *
 * @param source The GTFS source to use when building the cache (either an url or local path to a zip).
 * @param cache A path to save the cache to
 * @param transfers A function that builds transfers from one stop to other nearby stops. By default,
 * uses straight line distance.
 * @param transferSearchDistance The max distance stops should be searched for when building transfers
 */
@RaptorCacheDsl
public suspend fun RaptorCacheBuilder(
    source: String,
    cache: String,
    transfers: (Stop, List<Stop>) -> List<Transfer> = DefaultTransferMapper,
    transferSearchDistance: Double = 500.0
): Unit = gtfs(source, dbPath = "") {
    val logger = LoggerFactory.getLogger("RaptorCacheBuilder")
    val database = getDatabase(cache)

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
    val allStopsIndex = allStops.associateBy { it.id }
    // Compute estimates of footpath transfers
    val tree = RTree.create(
        allStops
            .map { EntryDefault.entry(it.id, Geometries.pointGeographic(it.longitude!!, it.latitude!!)) })

    val allTransfers = allStops.flatMap { stop ->
        val results = tree.search(
            Geometries.circle(
                stop.longitude!!,
                stop.latitude!!,
                convertLength(transferSearchDistance, to = Units.Degrees)
            )
        )
        val nearbyStops = results.asSequence().filter { it.value() != stop.id }.map { entry ->
            allStopsIndex.getValue(entry.value())
        }
        transfers(stop, nearbyStops.toList())
    }

    database.transaction {
        allTransfers.forEach { (from, to, distance, geometry) ->
            database.transferQueries.insert(from, to, distance, geometry)
        }
    }
}