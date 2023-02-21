package ca.derekellis.kgtfs.read

import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import ca.derekellis.kgtfs.db.GtfsDatabase
import java.time.LocalDate

@DslMarker
internal annotation class GtfsScopeDsl

@RequiresOptIn(
  message = "Internal GtfsScope APIs should be used as part of extending kgtfs functionality, not just for one-off queries.",
  level = RequiresOptIn.Level.ERROR
)
public annotation class InternalGtfsScopeApi

@GtfsScopeDsl
public class GtfsScope internal constructor(
  private val driver: SqlDriver,
  private val database: GtfsDatabase
) {
  @GtfsScopeDsl
  public val stops: StopsAccessor by lazy { StopsAccessor(database) }

  @GtfsScopeDsl
  public val calendars: CalendarAccessor by lazy { CalendarAccessor(database) }

  @GtfsScopeDsl
  public val trips: TripsAccessor by lazy { TripsAccessor(database) }

  /**
   * Computes the range of dates that this GTFS dataset covers.
   */
  @GtfsScopeDsl
  public fun serviceRange(): ClosedRange<LocalDate> {
    var min = LocalDate.MAX
    var max = LocalDate.MIN

    calendars.all()
      .ifEmpty { throw IllegalStateException("No calendars found.") }
      .forEach { calendar ->
        if (calendar.startDate < min) {
          min = calendar.startDate
        }
        if (calendar.endDate < max) {
          max = calendar.endDate
        }
      }
    return min..max
  }

  @GtfsScopeDsl
  @InternalGtfsScopeApi
  public fun <T> rawQuery(
    sql: String,
    mapper: (SqlCursor) -> T,
    parameters: Int = 0,
    binders: (SqlPreparedStatement.() -> Unit)? = null
  ): T = driver.executeQuery(null, sql, mapper, parameters, binders).value
}
