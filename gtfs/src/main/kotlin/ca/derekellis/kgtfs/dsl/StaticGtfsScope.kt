package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.RouteMapper
import ca.derekellis.kgtfs.db.ShapeMapper
import ca.derekellis.kgtfs.db.StopMapper
import ca.derekellis.kgtfs.db.StopTimeMapper
import ca.derekellis.kgtfs.db.TripMapper
import ca.derekellis.kgtfs.domain.model.Route
import ca.derekellis.kgtfs.domain.model.Shape
import ca.derekellis.kgtfs.domain.model.ShapeId
import ca.derekellis.kgtfs.domain.model.Stop
import ca.derekellis.kgtfs.domain.model.StopId
import ca.derekellis.kgtfs.domain.model.StopTime
import ca.derekellis.kgtfs.domain.model.Trip
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class StaticGtfsScope(
    public val stops: StopDsl,
    public val calendar: CalendarDsl,
    public val stopTimes: StopTimeDsl,
    public val trips: TripDsl,
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
     * Get all stop times in a given trip, in ascending order
     */
    public val Trip.stopTimes: List<StopTime>
        get() = database.stopTimeQueries.getByTripId(this.id, StopTimeMapper).executeAsList()


    public fun Trip.timeAtStop(stop: StopId): StopTime =
        database.stopTimeQueries.getByTripIdAtStop(this.id, stop, StopTimeMapper).executeAsOne()

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
