package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.ServiceId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.time.LocalDate

public object CalendarDates : Table() {
  public val serviceId: Column<String> = text("service_id")
  public val date: Column<LocalDate> = date("date")
  public val exceptionType: Column<Int> = integer("exception_type")

  public val Mapper: (ResultRow) -> CalendarDate = {
    CalendarDate(it[serviceId].let(::ServiceId), it[date], it[exceptionType])
  }

  public fun insert(calendarDate: CalendarDate): InsertStatement<Number> = insert {
    it[serviceId] = calendarDate.serviceId.value
    it[date] = calendarDate.date
    it[exceptionType] = calendarDate.exceptionType
  }
}
