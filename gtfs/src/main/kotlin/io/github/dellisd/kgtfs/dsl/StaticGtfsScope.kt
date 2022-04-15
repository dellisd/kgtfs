package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.RouteMapper
import io.github.dellisd.kgtfs.db.ShapeMapper
import io.github.dellisd.kgtfs.db.StopMapper
import io.github.dellisd.kgtfs.db.TripMapper
import io.github.dellisd.kgtfs.domain.model.Route
import io.github.dellisd.kgtfs.domain.model.Shape
import io.github.dellisd.kgtfs.domain.model.ShapeId
import io.github.dellisd.kgtfs.domain.model.Stop
import io.github.dellisd.kgtfs.domain.model.Trip
import me.tatarka.inject.annotations.Inject

@Inject
public class StaticGtfsScope(
    public val stops: StopDsl,
    public val calendar: CalendarDsl,
    private val database: GtfsDatabase
) {
    /**
     * List of all routes that service a given stop
     */
    public val Stop.routes: List<Route>
        get() = database.routeQueries.getByStopId(this.id, RouteMapper).executeAsList()

    /**
     * List of all trips that stop at a given stop
     */
    public val Stop.trips: List<Trip>
        get() = database.tripQueries.getByStopId(this.id, TripMapper).executeAsList()

    /**
     * Get all stops in a given trip, in ascending order
     */
    public val Trip.stops: List<Stop>
        get() = database.stopQueries.getByTripId(this.id, StopMapper).executeAsList()

    /**
     * Get the route for a given trip
     */
    public val Trip.route: Route
        get() = database.routeQueries.getById(this.routeId, RouteMapper).executeAsOne()

    /**
     * Get the shape for a given trip, or null if the shape doesn't exist
     */
    public val Trip.shape: List<Shape>?
        get() = this.shapeId?.let { database.shapeQueries.getById(it, ShapeMapper).executeAsList() }

    /**
     * Get all the shapes for a given route
     */
    public val Route.shapes: Map<ShapeId, List<Shape>>
        get() = database.shapeQueries.getByRouteId(this.id, ShapeMapper).executeAsList().groupBy { it.id }
}
