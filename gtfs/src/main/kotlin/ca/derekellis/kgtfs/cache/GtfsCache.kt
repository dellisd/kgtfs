package ca.derekellis.kgtfs.cache

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.db.migrateIfNeeded
import ca.derekellis.kgtfs.db.withGtfsSchema
import ca.derekellis.kgtfs.io.GtfsReader
import ca.derekellis.kgtfs.read.GtfsScope
import java.nio.file.Path

public class GtfsCache private constructor(
  private val driver: SqlDriver,
) : AutoCloseable {
  internal val database = driver.withGtfsSchema().apply { migrateIfNeeded(driver as JdbcSqliteDriver) }

  private val gtfsScope by lazy { GtfsScope(driver, database) }

  public fun <R> read(block: GtfsScope.() -> R): R = gtfsScope.block()

  private fun writeAgencies(agencies: Sequence<Agency>) = database.transaction {
    agencies.withEach {
      database.agencyQueries.insert(id, name, url, timezone, lang, phone, fareUrl, email)
    }
  }

  private fun writeStops(stops: Sequence<Stop>) = database.transaction {
    stops.withEach {
      database.stopQueries.insert(id, code, name, description, latitude, longitude, zoneId, url, locationType)
    }
  }

  private fun writeTrips(trips: Sequence<Trip>) = database.transaction {
    trips.withEach {
      database.tripQueries.insert(routeId, serviceId, id, headsign, directionId, blockId, shapeId)
    }
  }

  private fun writeStopTimes(stopTimes: Sequence<StopTime>) = database.transaction {
    stopTimes.withEach {
      database.stopTimeQueries.insert(tripId, arrivalTime, departureTime, stopId, stopSequence, pickupType, dropOffType)
    }
  }

  private fun writeCalendars(calendars: Sequence<Calendar>) = database.transaction {
    calendars.withEach {
      database.calendarQueries.insert(
        serviceId,
        monday,
        tuesday,
        wednesday,
        thursday,
        friday,
        saturday,
        sunday,
        startDate,
        endDate
      )
    }
  }

  private fun writeShapes(shapes: Sequence<Shape>) = database.transaction {
    shapes.withEach {
      database.shapeQueries.insert(id, latitude, longitude, sequence)
    }
  }

  private fun writeRoutes(routes: Sequence<Route>) = database.transaction {
    routes.withEach {
      database.routeQueries.insert(id, shortName, longName, desc, type, url, color, textColor)
    }
  }

  private fun writeCalendarDates(dates: Sequence<CalendarDate>) = database.transaction {
    dates.withEach {
      database.calendarDateQueries.insert(serviceId, date, exceptionType)
    }
  }

  override fun close() {
    driver.close()
  }

  public companion object {
    public fun open(path: Path): GtfsCache = GtfsCache(JdbcSqliteDriver("jdbc:sqlite:$path"))

    public fun fromReader(path: Path, reader: GtfsReader): GtfsCache = open(path).apply {
      reader.readAgencies(::writeAgencies)
      reader.readStops(::writeStops)
      reader.readCalendars(::writeCalendars)
      reader.readCalendarDates(::writeCalendarDates)
      reader.readRoutes(::writeRoutes)
      reader.readShapes(::writeShapes)
      reader.readTrips(::writeTrips)
      reader.readStopTimes(::writeStopTimes)
    }

    private fun <T> Sequence<T>.withEach(block: T.() -> Unit) = forEach { it.block() }
  }
}
