package ca.derekellis.kgtfs.csv

import java.time.LocalDate
import java.time.format.DateTimeFormatter

public typealias CsvFactory<T> = Map<String, String>.() -> T

private fun Map<String, String>.getBoolean(key: String): Boolean = when (get(key)) {
  "1" -> true
  else -> false
}

private val datePattern = DateTimeFormatter.ofPattern("yyyyMMdd")
private fun Map<String, String>.getLocalDate(key: String): LocalDate = LocalDate.parse(getValue(key), datePattern)

@InternalKgtfsApi
public val AgencyFactory: CsvFactory<Agency> = {
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

@InternalKgtfsApi
public val CalendarFactory: CsvFactory<Calendar> = {
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

@InternalKgtfsApi
public val CalendarDateFactory: CsvFactory<CalendarDate> = {
  CalendarDate(
    serviceId = ServiceId(getValue("service_id")),
    date = getLocalDate("date"),
    exceptionType = getValue("exception_type").toInt(),
  )
}

@InternalKgtfsApi
public val RouteFactory: CsvFactory<Route> = {
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

@InternalKgtfsApi
public val StopFactory: CsvFactory<Stop> = {
  Stop(
    id = StopId(getValue("stop_id")),
    code = get("stop_code"),
    name = get("stop_name"),
    description = get("stop_desc"),
    latitude = get("stop_lat")?.toDouble(),
    longitude = get("stop_lon")?.toDouble(),
    zoneId = get("zone_id"),
    url = get("stop_url"),
    locationType = Stop.LocationType.entries[getValue("location_type").toInt()],
    parentStation = get("parent_station")?.let(::StopId),
    timezone = get("stop_timezone"),
    wheelchairBoarding = get("wheelchair_boarding")?.toInt(),
    levelId = get("level_id"),
    platformCode = get("platform_code"),
  )
}

@InternalKgtfsApi
public val ShapeFactory: CsvFactory<Shape> = {
  Shape(
    id = ShapeId(getValue("shape_id")),
    latitude = getValue("shape_pt_lat").toDouble(),
    longitude = getValue("shape_pt_lon").toDouble(),
    sequence = getValue("shape_pt_sequence").toInt(),
  )
}

@InternalKgtfsApi
public val TripFactory: CsvFactory<Trip> = {
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

@InternalKgtfsApi
public val StopTimeFactory: CsvFactory<StopTime> = {
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
