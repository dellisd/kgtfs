package ca.derekellis.kgtfs.db2

import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.ServiceId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object CalendarDates : Table() {
  val serviceId: Column<String> = text("service_id")
  val date: Column<LocalDate> = date("date")
  val exceptionType: Column<Int> = integer("exception_type")

  val Mapper: (ResultRow) -> CalendarDate = {
    CalendarDate(it[serviceId].let(::ServiceId), it[date], it[exceptionType])
  }

  fun insert(calendarDate: CalendarDate) = insert {
    it[serviceId] = calendarDate.serviceId.value
    it[date] = calendarDate.date
    it[exceptionType] = calendarDate.exceptionType
  }
}
