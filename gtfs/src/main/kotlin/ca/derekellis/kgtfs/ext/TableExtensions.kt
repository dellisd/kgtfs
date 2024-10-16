package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.GtfsDbScope
import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.db.CalendarDates
import ca.derekellis.kgtfs.db.Calendars
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate

/**
 * Compute the range of dates that this GTFS dataset covers.
 */
public fun GtfsDbScope.serviceRange(): ClosedRange<LocalDate> {
  var min = LocalDate.MAX
  var max = LocalDate.MIN

  Calendars.selectAll().map(Calendars.Mapper)
    .ifEmpty { throw IllegalStateException("No calendars found.") }
    .forEach { calendar ->
      if (calendar.startDate < min) {
        min = calendar.startDate
      }
      if (calendar.endDate > max) {
        max = calendar.endDate
      }
    }
  return min..max
}

/**
 * Get the set of [Calendar] objects for a given [date].
 *
 * @see today
 */
public fun Calendars.onDate(date: LocalDate): Set<Calendar> {
  val calendarDates = CalendarDates.select { CalendarDates.date eq date }.map(CalendarDates.Mapper).associateBy { it.serviceId }
  val calendars = Calendars.select { (Calendars.startDate lessEq date) and (Calendars.endDate greaterEq date) }.map(Mapper)

  val predicate = when (date.dayOfWeek.value) {
    1 -> Calendar::monday
    2 -> Calendar::tuesday
    3 -> Calendar::wednesday
    4 -> Calendar::thursday
    5 -> Calendar::friday
    6 -> Calendar::saturday
    else -> Calendar::sunday
  }

  return calendars
    .filter { predicate(it) || calendarDates[it.serviceId]?.exceptionType == 1 }
    .filter { calendarDates[it.serviceId]?.exceptionType != 2 }
    .toSet()
}

public fun Calendars.today(): Set<Calendar> = onDate(LocalDate.now())
