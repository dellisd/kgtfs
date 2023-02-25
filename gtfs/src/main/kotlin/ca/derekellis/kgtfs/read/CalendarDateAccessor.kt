package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.db.CalendarDateMapper
import ca.derekellis.kgtfs.db.GtfsDatabase
import java.time.LocalDate

public class CalendarDateAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun all(): List<CalendarDate> = database.calendarDateQueries.getAll(CalendarDateMapper).executeAsList()

  @GtfsScopeDsl
  public fun byDate(date: LocalDate): List<CalendarDate> = database.calendarDateQueries.getByDate(date, CalendarDateMapper).executeAsList()

  @GtfsScopeDsl
  public fun today(): List<CalendarDate> = byDate(LocalDate.now())
}