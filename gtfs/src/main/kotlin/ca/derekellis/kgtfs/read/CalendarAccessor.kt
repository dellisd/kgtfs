package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.db.CalendarDateMapper
import ca.derekellis.kgtfs.db.CalendarMapper
import ca.derekellis.kgtfs.db.GtfsDatabase
import java.time.LocalDate

public class CalendarAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun onDate(date: LocalDate): Set<Calendar> {
    val exceptions =
      database.calendarDateQueries.getByDate(date, CalendarDateMapper).executeAsList()
        .associateBy { it.serviceId }

    val predicate = when (date.dayOfWeek.value) {
      1 -> Calendar::monday
      2 -> Calendar::tuesday
      3 -> Calendar::wednesday
      4 -> Calendar::thursday
      5 -> Calendar::friday
      6 -> Calendar::saturday
      else -> Calendar::sunday
    }

    val calendars =
      database.calendarQueries.getByDate(date, CalendarMapper).executeAsList()

    return calendars
      .filter { predicate(it) || exceptions[it.serviceId]?.exceptionType == 1 }
      .filter { exceptions[it.serviceId]?.exceptionType != 2 }
      .toSet()
  }

  @GtfsScopeDsl
  public fun today(): Set<Calendar> = onDate(LocalDate.now())

  @GtfsScopeDsl
  public fun all(): List<Calendar> = database.calendarQueries.getAll(CalendarMapper).executeAsList()
}
