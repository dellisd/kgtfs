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
import ca.derekellis.kgtfs.read.GtfsScope
import java.nio.file.Path

public class GtfsCache private constructor(
  private val driver: SqlDriver,
) : AutoCloseable {
  internal val database = driver.withGtfsSchema().apply { migrateIfNeeded(driver as JdbcSqliteDriver) }

  private val gtfsScope by lazy { GtfsScope(driver, database) }

  public fun <R> read(block: GtfsScope.() -> R): R = gtfsScope.block()

  internal fun writeAgencies(agencies: Sequence<Agency>) = database.transaction {
    agencies.withEach {
      database.agencyQueries.insert(id, name, url, timezone, lang, phone, fareUrl, email)
    }
  }

  internal fun writeStops(stops: Sequence<Stop>) = database.transaction {
    stops.withEach {
      database.stopQueries.insert(id, code, name, description, latitude, longitude, zoneId, url, locationType)
    }
  }

  internal fun writeTrips(trips: Sequence<Trip>) = database.transaction {
    trips.withEach {
      database.tripQueries.insert(routeId, serviceId, id, headsign, directionId, blockId, shapeId)
    }
  }

  internal fun writeStopTimes(stopTimes: Sequence<StopTime>) = database.transaction {
    stopTimes.withEach {
      database.stopTimeQueries.insert(tripId, arrivalTime, departureTime, stopId, stopSequence, pickupType, dropOffType)
    }
  }

  internal fun writeCalendars(calendars: Sequence<Calendar>) = database.transaction {
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

  internal fun writeShapes(shapes: Sequence<Shape>) = database.transaction {
    shapes.withEach {
      database.shapeQueries.insert(id, latitude, longitude, sequence)
    }
  }

  internal fun writeRoutes(routes: Sequence<Route>) = database.transaction {
    routes.withEach {
      database.routeQueries.insert(id, shortName, longName, desc, type, url, color, textColor)
    }
  }

  internal fun writeCalendarDates(dates: Sequence<CalendarDate>) = database.transaction {
    dates.withEach {
      database.calendarDateQueries.insert(serviceId, date, exceptionType)
    }
  }

  private fun <T> Sequence<T>.withEach(block: T.() -> Unit) = forEach { it.block() }

  override fun close() {
    driver.close()
  }

  public companion object {
    public fun open(path: Path): GtfsCache = GtfsCache(JdbcSqliteDriver("jdbc:sqlite:$path"))
  }
}
