package ca.derekellis.kgtfs.raptor.providers

import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId
import ca.derekellis.kgtfs.raptor.RaptorDataProvider
import ca.derekellis.kgtfs.raptor.db.executeAsSet
import ca.derekellis.kgtfs.raptor.db.getDatabase
import ca.derekellis.kgtfs.raptor.models.StopTime
import ca.derekellis.kgtfs.raptor.models.Transfer

public class SqliteProvider(dbPath: String) : RaptorDataProvider {
    private val routesAtStop: MutableMap<StopId, Set<RouteId>> = mutableMapOf()

    private val database = getDatabase(dbPath)

    init {
        database.routeAtStopQueries.getAll().executeAsList().groupBy { it.stop_id }.forEach { (stop, list) ->
            routesAtStop[stop] = list.map { it.route_id }.toSet()
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
