package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.Calendar
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.lessEq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
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
  val calendarDates = CalendarDates.select(CalendarDates.columns).where { CalendarDates.date eq date }.map(CalendarDates.Mapper).associateBy { it.serviceId }
  val calendars = Calendars.select(Calendars.columns).where { (Calendars.startDate lessEq date) and (Calendars.endDate greaterEq date) }.map(Mapper)

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
