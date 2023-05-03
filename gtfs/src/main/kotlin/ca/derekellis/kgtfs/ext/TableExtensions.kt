package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.db2.CalendarDates
import ca.derekellis.kgtfs.db2.Calendars
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.time.LocalDate

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
