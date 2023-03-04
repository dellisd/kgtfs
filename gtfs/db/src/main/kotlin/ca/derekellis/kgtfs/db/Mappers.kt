package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.AgencyId
import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.ServiceId
import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.ShapeId
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.csv.TripId
import java.time.LocalDate

public val StopMapper: (StopId, String?, String?, String?, Double?, Double?, String?, String?, Stop.LocationType?) -> Stop =
    { stop_id: StopId, stop_code: String?, stop_name: String?, stop_desc: String?, stop_lat: Double?, stop_lon: Double?, zone_id: String?, stop_url: String?, location_type: Stop.LocationType? ->
        Stop(stop_id, stop_code, stop_name, stop_desc, stop_lat, stop_lon, zone_id, stop_url, location_type)
    }

public val RouteMapper: (RouteId, String?, String?, String?, Route.Type, String?, String?, String?) -> Route =
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

public val TripMapper: (RouteId, ServiceId, TripId, String?, Int?, String?, ShapeId?) -> Trip =
    { route_id: RouteId, service_id: ServiceId, trip_id: TripId, trip_headsign: String?, direction_id: Int?, block_id: String?, shape_id: ShapeId? ->
        Trip(route_id, service_id, trip_id, trip_headsign, null, direction_id, block_id, shape_id)
    }

public val ShapeMapper: (ShapeId, Double, Double, Int) -> Shape = { shape_id: ShapeId, shape_pt_lat: Double, shape_pt_lon: Double, shape_pt_sequence: Int ->
    Shape(shape_id, shape_pt_lat, shape_pt_lon, shape_pt_sequence)
}

public val CalendarMapper: (ServiceId, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, LocalDate, LocalDate) -> Calendar =
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

public val CalendarDateMapper: (ServiceId, LocalDate, Int) -> CalendarDate =
    { service_id: ServiceId, date: LocalDate, exception_type: Int -> CalendarDate(service_id, date, exception_type) }

public val StopTimeMapper: (TripId, GtfsTime, GtfsTime, StopId, Int, Int?, Int?) -> StopTime =
    { trip_id: TripId, arrival_time: GtfsTime, departure_time: GtfsTime, stop_id: StopId, stop_sequence: Int, pickup_type: Int?, drop_off_type: Int? ->
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

public val AgencyMapper: (AgencyId?, String, String, String, String?, String?, String?, String?) -> Agency =
    { agency_id: AgencyId?, agency_name: String, agency_url: String, agency_timezeone: String, agency_lang: String?, agency_phone: String?, agency_fare_url: String?, agency_email: String? ->
        Agency(
            agency_id,
            agency_name,
            agency_url,
            agency_timezeone,
            agency_lang,
            agency_phone,
            agency_fare_url,
            agency_email
        )
    }