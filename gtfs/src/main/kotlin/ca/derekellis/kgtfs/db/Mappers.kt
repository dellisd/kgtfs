package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.domain.model.Calendar
import ca.derekellis.kgtfs.domain.model.CalendarDate
import ca.derekellis.kgtfs.domain.model.Route
import ca.derekellis.kgtfs.domain.model.RouteId
import ca.derekellis.kgtfs.domain.model.ServiceId
import ca.derekellis.kgtfs.domain.model.Shape
import ca.derekellis.kgtfs.domain.model.ShapeId
import ca.derekellis.kgtfs.domain.model.Stop
import ca.derekellis.kgtfs.domain.model.StopId
import ca.derekellis.kgtfs.domain.model.StopTime
import ca.derekellis.kgtfs.domain.model.Trip
import ca.derekellis.kgtfs.domain.model.TripId
import java.time.LocalDate

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
    { route_id: RouteId, service_id: ServiceId, trip_id: TripId, trip_headsign: String?, direction_id: Int?, block_id: String?, shape_id: ShapeId? ->
        Trip(route_id, service_id, trip_id, trip_headsign, null, direction_id, block_id, shape_id)
    }

internal val ShapeMapper = { shape_id: ShapeId, shape_pt_lat: Double, shape_pt_lon: Double, shape_pt_sequence: Int ->
    Shape(shape_id, shape_pt_lat, shape_pt_lon, shape_pt_sequence)
}

internal val CalendarMapper =
    { service_id: ServiceId, monday: Boolean, tuesday: Boolean, wednesday: Boolean, thursday: Boolean, friday: Boolean, saturday: Boolean, sunday: Boolean, start_date: LocalDate, end_date: LocalDate ->
        Calendar(
            service_id,
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday,
            start_date,
            end_date
        )
    }

internal val CalendarDateMapper =
    { service_id: ServiceId, date: LocalDate, exception_type: Int -> CalendarDate(service_id, date, exception_type) }

internal val StopTimeMapper =
    { trip_id: TripId, arrival_time: String, departure_time: String, stop_id: StopId, stop_sequence: Int, pickup_type: Int?, drop_off_type: Int? ->
        StopTime(
            trip_id,
            arrival_time,
            departure_time,
            stop_id,
            stop_sequence,
            pickupType = pickup_type,
            dropOffType = drop_off_type
        )
    }