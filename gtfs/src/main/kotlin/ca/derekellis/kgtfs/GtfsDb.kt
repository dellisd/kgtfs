package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.io.GtfsReader
import ca.derekellis.kgtfs.io.GtfsWriter
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path
import kotlin.io.path.pathString

public class GtfsDb private constructor(public val path: String) {
  private val database = Database.connect("jdbc:sqlite:$path")

  @ExperimentalKgtfsApi
  @GtfsDsl
  public fun <T> query(logger: SqlLogger? = null, statement: GtfsDbScope.() -> T): T = transaction(database) {
    logger?.let { addLogger(logger) }
    GtfsDbScope().statement()
  }

  override fun toString(): String = "GtfsDb($path)"
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as GtfsDb

    return path == other.path
  }

  override fun hashCode(): Int {
    return path.hashCode()
  }

  @ExperimentalKgtfsApi
  public fun intoWriter(writer: GtfsWriter) {
    query {
      writer.writeAgencies(Agencies.selectAll().asSequence().map(Agencies.Mapper))
      writer.writeCalendars(Calendars.selectAll().asSequence().map(Calendars.Mapper))
      writer.writeCalendarDates(CalendarDates.selectAll().asSequence().map(CalendarDates.Mapper))
      writer.writeStops(Stops.selectAll().asSequence().map(Stops.Mapper))
      writer.writeRoutes(Routes.selectAll().asSequence().map(Routes.Mapper))
      writer.writeTrips(Trips.selectAll().asSequence().map(Trips.Mapper))
      writer.writeStopTimes(StopTimes.selectAll().asSequence().map(StopTimes.Mapper))
      writer.writeShapes(Shapes.selectAll().asSequence().map(Shapes.Mapper))
    }
  }

  public companion object {
    /**
     * Create a SQLite GTFS database from a [GtfsReader].
     *
     * @param path A SQLite-compatible path to save the database into.
     */
    @OptIn(ExperimentalKgtfsApi::class)
    public fun fromReader(reader: GtfsReader, path: String): GtfsDb {
      val db = GtfsDb(path)
      db.query {
        SchemaUtils.create(Agencies, Stops, Calendars, CalendarDates, Routes, Shapes, Trips, StopTimes)
        reader.readAgencies { agencies -> agencies.forEach(Agencies::insert) }
        reader.readStops { stops -> stops.forEach(Stops::insert) }
        reader.readCalendars { calendars -> calendars.forEach(Calendars::insert) }
        reader.readCalendarDates { calendarDates -> calendarDates.forEach(CalendarDates::insert) }
        reader.readRoutes { routes -> routes.forEach(Routes::insert) }
        reader.readShapes { shapes -> shapes.forEach(Shapes::insert) }
        reader.readTrips { trips -> trips.forEach(Trips::insert) }
        reader.readStopTimes { stopTimes -> stopTimes.forEach(StopTimes::insert) }
      }
      return db
    }

    /**
     * Create a SQLite GTFS database from a [GtfsReader].
     *
     * @param path The path to save the database to.
     */
    public fun fromReader(reader: GtfsReader, path: Path): GtfsDb {
      return fromReader(reader, path.pathString)
    }

    @OptIn(ExperimentalKgtfsApi::class)
    public fun open(path: String): GtfsDb {
      val db = GtfsDb(path)
      db.query {
        SchemaUtils.create(Agencies, Stops, Calendars, CalendarDates, Routes, Shapes, Trips, StopTimes)
      }
      return GtfsDb(path)
    }

    /**
     * Open an existing database located at [path].
     */
    public fun open(path: Path): GtfsDb {
      return open(path.pathString)
    }
  }
}
