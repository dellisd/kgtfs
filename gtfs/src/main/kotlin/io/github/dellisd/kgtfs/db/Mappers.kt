package io.github.dellisd.kgtfs.db

import io.github.dellisd.kgtfs.domain.model.Route
import io.github.dellisd.kgtfs.domain.model.RouteId
import io.github.dellisd.kgtfs.domain.model.ServiceId
import io.github.dellisd.kgtfs.domain.model.Stop
import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.Trip
import io.github.dellisd.kgtfs.domain.model.TripId

internal val StopMapper =
    { stop_id: StopId, stop_code: String?, stop_name: String?, stop_desc: String?, stop_lat: Double?, stop_lon: Double?, zone_id: String?, stop_url: String?, location_type: Stop.LocationType? ->
        Stop(stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type)
    }

internal val RouteMapper =
    { route_id: RouteId, route_short_name: String?, route_long_name: String?, route_desc: String?, route_type: Route.Type, route_url: String?, route_color: String?, route_text_color: String? ->
        Route(
            route_id,
            route_short_name,
            route_long_name,
            route_desc,
            route_type,
            route_url,
            route_color,
            route_text_color
        )
    }

internal val TripMapper =
    { route_id: RouteId, service_id: ServiceId, trip_id: TripId, trip_headsign: String?, direction_id: Int?, block_id: String?, shape_id: String? ->
        Trip(route_id, service_id, trip_id, trip_headsign, null, direction_id, block_id, shape_id)
    }