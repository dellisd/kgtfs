package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.io.GtfsReader
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path

public class GtfsDb private constructor(public val path: Path?) {
  private val database = Database.connect("jdbc:sqlite:${path ?: ""}")

  @ExperimentalKgtfsApi
  @GtfsDsl
  public fun <T> query(logger: SqlLogger? = null, statement: GtfsDbScope.() -> T): T = transaction(database) {
    logger?.let { addLogger(logger) }
    GtfsDbScope().statement()
  }

  override fun toString(): String = "GtfsDb(${path ?: "IN MEMORY"})"
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as GtfsDb

    return path == other.path
  }

  override fun hashCode(): Int {
    return path.hashCode()
  }


  public companion object {
    /**
     * Create a SQLite GTFS database from a [GtfsReader].
     *
     * @param into The path to save the database to, or `null` to create an in-memory database.
     */
    @OptIn(ExperimentalKgtfsApi::class)
    public fun fromReader(reader: GtfsReader, into: Path?): GtfsDb {
      val db = GtfsDb(into)
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
     * Open an existing database located at [path].
     */
    public fun open(path: Path): GtfsDb = GtfsDb(path)
  }
}
