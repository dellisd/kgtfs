package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsDsl
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

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

  public companion object {
    @OptIn(ExperimentalKgtfsApi::class)
    public fun fromReader(reader: ca.derekellis.kgtfs.GtfsReader, path: String): GtfsDb {
      val db = GtfsDb(path)

      db.query {
        SchemaUtils.create(Agencies, Stops, Calendars, CalendarDates, Routes, Shapes, Trips, StopTimes)
        reader.agency().asSequence().forEach(Agencies::insert)
        reader.stops().asSequence().forEach(Stops::insert)
        reader.calendar().asSequence().forEach(Calendars::insert)
        reader.calendarDates().asSequence().forEach(CalendarDates::insert)
        reader.routes().asSequence().forEach(Routes::insert)
        reader.shapes().asSequence().forEach(Shapes::insert)
        reader.trips().asSequence().forEach(Trips::insert)
        reader.stopTimes().asSequence().forEach(StopTimes::insert)
      }

      return db
    }

    @OptIn(ExperimentalKgtfsApi::class)
    public fun open(path: String): GtfsDb {
      val db = GtfsDb(path)
      db.query {
        SchemaUtils.create(Agencies, Stops, Calendars, CalendarDates, Routes, Shapes, Trips, StopTimes)
      }
      return GtfsDb(path)
    }
  }
}
