package ca.derekellis.kgtfs.db

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.github.dellisd.kgtfs.db.Agency
import io.github.dellisd.kgtfs.db.Calendar
import io.github.dellisd.kgtfs.db.CalendarDate
import io.github.dellisd.kgtfs.db.Metadata
import io.github.dellisd.kgtfs.db.Route
import io.github.dellisd.kgtfs.db.Shape
import io.github.dellisd.kgtfs.db.Stop
import io.github.dellisd.kgtfs.db.StopTime
import io.github.dellisd.kgtfs.db.Trip

internal fun SqlDriver.withGtfsSchema(): GtfsDatabase = GtfsDatabase(
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
