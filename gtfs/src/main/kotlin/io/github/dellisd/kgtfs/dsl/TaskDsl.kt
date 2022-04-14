package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.Route
import io.github.dellisd.kgtfs.db.Stop
import io.github.dellisd.kgtfs.db.Trip
import me.tatarka.inject.annotations.Inject

@Inject
public class TaskDsl(public val stops: StopDsl, public val calendar: CalendarDsl, private val database: GtfsDatabase) {
    /**
     * List of all routes that service a given stop
     */
    public val Stop.routes: List<Route>
        get() = database.routeQueries.getByStopId(this.stop_id).executeAsList()

    /**
     * List of all trips that stop at a given stop
     */
    public val Stop.trips: List<Trip>
        get() = database.tripQueries.getByStopId(this.stop_id).executeAsList()

    /**
     * Get all stops in a given trip, in ascending order
     */
    public val Trip.stops: List<Stop>
        get() = database.stopQueries.getByTripId(this.trip_id).executeAsList()

    /**
     * Get the route for a given trip
     */
    public val Trip.route: Route
        get() = database.routeQueries.getById(this.route_id).executeAsOne()
}
