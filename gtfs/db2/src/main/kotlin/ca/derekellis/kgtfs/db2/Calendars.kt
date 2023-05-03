package ca.derekellis.kgtfs.db2

import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.ServiceId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object Calendars : Table(name = "Calendar") {
  val serviceId: Column<String> = text("service_id")
  val monday: Column<Boolean> = bool("monday")
  val tuesday: Column<Boolean> = bool("tuesday")
  val wednesday: Column<Boolean> = bool("wednesday")
  val thursday: Column<Boolean> = bool("thursday")
  val friday: Column<Boolean> = bool("friday")
  val saturday: Column<Boolean> = bool("saturday")
  val sunday: Column<Boolean> = bool("sunday")
  val startDate: Column<LocalDate> = date("start_date")
  val endDate: Column<LocalDate> = date("end_date")

  override val primaryKey = PrimaryKey(serviceId)

  val Mapper: (ResultRow) -> Calendar = {
    Calendar(
      it[serviceId].let(::ServiceId),
      it[monday],
      it[tuesday],
      it[wednesday],
      it[thursday],
      it[friday],
      it[saturday],
      it[sunday],
      it[startDate],
      it[endDate]
    )
  }

  fun insert(calendar: Calendar) = insert {
    it[serviceId] = calendar.serviceId.value
    it[monday] = calendar.monday
    it[tuesday] = calendar.tuesday
    it[wednesday] = calendar.wednesday
    it[thursday] = calendar.thursday
    it[friday] = calendar.friday
    it[saturday] = calendar.saturday
    it[sunday] = calendar.sunday
    it[startDate] = calendar.startDate
    it[endDate] = calendar.endDate
  }
}
