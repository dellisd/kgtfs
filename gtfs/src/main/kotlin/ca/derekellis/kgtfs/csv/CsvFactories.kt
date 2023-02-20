package ca.derekellis.kgtfs.csv

import ca.derekellis.kgtfs.domain.model.Agency
import ca.derekellis.kgtfs.domain.model.AgencyId
import ca.derekellis.kgtfs.domain.model.Calendar
import ca.derekellis.kgtfs.domain.model.CalendarDate
import ca.derekellis.kgtfs.domain.model.GtfsTime
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
import java.time.format.DateTimeFormatter

internal typealias CsvFactory<T> = Map<String, String>.() -> T

private fun Map<String, String>.getBoolean(key: String): Boolean = when (get(key)) {
  "1" -> true
  else -> false
}

private val datePattern = DateTimeFormatter.ofPattern("yyyyMMdd")
private fun Map<String, String>.getLocalDate(key: String): LocalDate = LocalDate.parse(getValue(key), datePattern)

internal val AgencyFactory: CsvFactory<Agency> = {
  Agency(
    id = get("agency_id")?.let(::AgencyId),
    name = getValue("agency_name"),
    url = getValue("agency_url"),
    timezone = getValue("agency_timezone"),
    lang = get("agency_lang"),
    phone = get("agency_phone"),
    fareUrl = get("agency_fare_url"),
    email = get("agency_email"),
  )
}

internal val CalendarFactory: CsvFactory<Calendar> = {
  Calendar(
    serviceId = ServiceId(getValue("service_id")),
    monday = getBoolean("monday"),
    tuesday = getBoolean("tuesday"),
    wednesday = getBoolean("wednesday"),
    thursday = getBoolean("thursday"),
    friday = getBoolean("friday"),
    saturday = getBoolean("saturday"),
    sunday = getBoolean("sunday"),
    startDate = getLocalDate("start_date"),
    endDate = getLocalDate("end_date"),
  )
}

internal val CalendarDateFactory: CsvFactory<CalendarDate> = {
  CalendarDate(
    serviceId = ServiceId(getValue("service_id")),
    date = getLocalDate("date"),
    exceptionType = getValue("exception_type").toInt(),
  )
}

internal val RouteFactory: CsvFactory<Route> = {
  Route(
    id = RouteId(getValue("route_id")),
    shortName = get("route_short_name"),
    longName = get("route_long_name"),
    desc = get("route_desc"),
    type = Route.Type.valueMap.getValue(getValue("route_type").toInt()),
    url = get("route_url"),
    color = get("route_color"),
    textColor = get("route_text_color"),
  )
}

internal val StopFactory: CsvFactory<Stop> = {
  Stop(
    id = StopId(getValue("stop_id")),
    code = get("stop_code"),
    name = get("stop_name"),
    description = get("stop_desc"),
    latitude = get("stop_late")?.toDouble(),
    longitude = get("stop_lon")?.toDouble(),
    zoneId = get("zone_id"),
    url = get("stop_url"),
    locationType = Stop.LocationType.values()[getValue("location_type").toInt()],
    parentStation = get("parent_station")?.let(::StopId),
    timezone = get("stop_timezone"),
    wheelchairBoarding = get("wheelchair_boarding")?.toInt(),
    levelId = get("level_id"),
    platformCode = get("platform_code"),
  )
}

internal val ShapeFactory: CsvFactory<Shape> = {
  Shape(
    id = ShapeId(getValue("shape_id")),
    latitude = getValue("shape_pt_lat").toDouble(),
    longitude = getValue("shape_pt_lon").toDouble(),
    sequence = getValue("shape_pt_sequence").toInt(),
  )
}

internal val TripFactory: CsvFactory<Trip> = {
  Trip(
    routeId = RouteId(getValue("route_id")),
    serviceId = ServiceId(getValue("service_id")),
    id = TripId(getValue("trip_id")),
    headsign = get("trip_headsign"),
    shortName = get("trip_short_name"),
    directionId = get("direction_id")?.toInt(),
    blockId = get("block_id"),
    shapeId = get("shape_id")?.let(::ShapeId),
    wheelchairAccessible = get("wheelchair_accessible")?.toInt(),
    bikesAllowed = getBoolean("bikes_allowed"),
  )
}

internal val StopTimeFactory: CsvFactory<StopTime> = {
  StopTime(
    tripId = TripId(getValue("trip_id")),
    arrivalTime = getValue("arrival_time").let(::GtfsTime),
    departureTime = getValue("departure_time").let(::GtfsTime),
    stopId = getValue("stop_id").let(::StopId),
    stopSequence = getValue("stop_sequence").toInt(),
    stopHeadsign = get("stop_headsign"),
    pickupType = get("pickup_type")?.toInt(),
    dropOffType = get("drop_off_type")?.toInt(),
    continuousPickup = get("continuous_pickup")?.toInt(),
    continuousDropOff = get("continuous_drop_off")?.toInt(),
    shapeDistTraveled = get("shape_dist_travelled")?.toDouble(),
    timepoint = get("timepoint")?.toInt(),
  )
}
