package ca.derekellis.kgtfs.db

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver

public fun SqlDriver.withGtfsSchema(): GtfsDatabase = GtfsDatabase(
  this,
  StopAdapter = Stop.Adapter(
    StopIdAdapter,
    LocationTypeAdapter
  ),
  StopTimeAdapter = StopTime.Adapter(
    TripIdAdapter,
    GtfsTimeAdapter,
    GtfsTimeAdapter,
    StopIdAdapter,
    IntColumnAdapter,
    IntColumnAdapter,
    IntColumnAdapter
  ),
  TripAdapter = Trip.Adapter(
    RouteIdAdapter,
    ServiceIdAdapter,
    TripIdAdapter,
    IntColumnAdapter,
    ShapeIdAdapter
  ),
  MetadataAdapter = Metadata.Adapter(InstantColumnAdapter),
  CalendarAdapter = Calendar.Adapter(
    ServiceIdAdapter,
    LocalDateAdapter,
    LocalDateAdapter
  ),
  CalendarDateAdapter = CalendarDate.Adapter(
    ServiceIdAdapter,
    LocalDateAdapter, IntColumnAdapter
  ),
  RouteAdapter = Route.Adapter(RouteIdAdapter, RouteTypeAdapter),
  ShapeAdapter = Shape.Adapter(ShapeIdAdapter, IntColumnAdapter),
  AgencyAdapter = Agency.Adapter(AgencyIdAdapter)
)
