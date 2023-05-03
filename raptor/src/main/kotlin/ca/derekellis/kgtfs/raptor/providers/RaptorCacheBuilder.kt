package ca.derekellis.kgtfs.raptor.providers

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsDb
import com.github.davidmoten.rtree2.RTree
import com.github.davidmoten.rtree2.geometry.Geometries
import com.github.davidmoten.rtree2.internal.EntryDefault
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.ext.today
import ca.derekellis.kgtfs.ext.uniqueTripSequences
import ca.derekellis.kgtfs.raptor.db.getDatabase
import ca.derekellis.kgtfs.raptor.models.Transfer
import io.github.dellisd.spatialk.geojson.Position
import io.github.dellisd.spatialk.geojson.dsl.feature
import io.github.dellisd.spatialk.geojson.dsl.lineString
import io.github.dellisd.spatialk.geojson.dsl.lngLat
import io.github.dellisd.spatialk.turf.Units
import io.github.dellisd.spatialk.turf.convertLength
import io.github.dellisd.spatialk.turf.distance
import org.jetbrains.exposed.sql.selectAll

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
@OptIn(ExperimentalKgtfsApi::class)
@RaptorCacheDsl
public fun RaptorCacheBuilder(
  source: GtfsDb,
  cache: String,
  transfers: (Stop, List<Stop>) -> List<Transfer> = DefaultTransferMapper,
  transferSearchDistance: Double = 500.0
): Unit = source.query {
    val database = getDatabase(cache)

    // TODO: Handle different days
    val today = Calendars.today().map { it.serviceId }.toSet()

    val sequences = uniqueTripSequences(today)

    database.transaction {
        Stops.selectAll().map(Stops.Mapper).forEach { database.stopQueries.insert(it.id) }

        sequences.forEach {
            database.routeQueries.insert(it.uniqueId)
            it.sequence.forEachIndexed { index, stop ->
                database.routeAtStopQueries.insert(stop, it.uniqueId, index + 1)
            }

            it.trips.forEach { (trip, times) ->
                database.tripQueries.insert(trip, it.uniqueId)
                times.forEachIndexed { index, time ->
                    database.stopTimeQueries.insert(trip, time.stopId, time.arrivalTime, index + 1)
                }
            }
        }
    }

    val allStops = Stops.selectAll().map(Stops.Mapper)
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
