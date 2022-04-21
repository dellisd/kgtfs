package io.github.dellisd.raptor

import io.github.dellisd.kgtfs.domain.model.RouteId
import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.TripId
import io.github.dellisd.raptor.models.GtfsTime
import io.github.dellisd.raptor.models.StopTime
import io.github.dellisd.raptor.models.Transfer

public interface RaptorDataProvider {
    /**
     * Get a set of routes that serve the given stop.
     *
     * @param stop the stop
     * @return A set of routes that serve the stop
     */
    public fun getRoutesAtStop(stop: StopId): Set<RouteId>

    /**
     * Get an ordered list of stops along the given route
     *
     * @param route the route
     * @return The list of stops that the route serves
     */
    public fun getStopsAlongRoute(route: RouteId): List<StopId>

    /**
     * Get the times that a given trip stops at each stop along the trip
     *
     * @param trip the trip
     * @return A list of [StopTime] with the time that the trip stops at each [StopTime.stop].
     */
    public fun getStopTimes(trip: TripId): List<StopTime>

    /**
     * Get the earliest trip that can be caught at a given stop along a specific route.
     *
     * @param route The route
     * @param index The index of the stop along the route. The index is preferred over a stop id since a route may visit the same stop multiple times in a single trip
     * @param after Trips are only searched after this time (i.e. the earliest possible time)
     * @return The earliest trip, or null if no such trip exists
     */
    public fun getEarliestTripAtStop(route: RouteId, index: Int, after: GtfsTime): TripId?

    /**
     * Get a set of transfers (between stops) that can be made at a given stop.
     *
     * @return A set of transfers. The [Transfer.from] property of each entry will always be equal to [stop].
     */
    public fun getTransfersAtStop(stop: StopId): Set<Transfer>

    /**
     * @return `true` if [a] comes before [b] in the given [route], `false` otherwise
     */
    public fun isStopBefore(route: RouteId, a: StopId, b: StopId): Boolean
}
