package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.ServiceId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.statements.InsertStatement
import java.time.LocalDate

public object Calendars : Table(name = "Calendar") {
  public val serviceId: Column<String> = text("service_id")
  public val monday: Column<Boolean> = bool("monday")
  public val tuesday: Column<Boolean> = bool("tuesday")
  public val wednesday: Column<Boolean> = bool("wednesday")
  public val thursday: Column<Boolean> = bool("thursday")
  public val friday: Column<Boolean> = bool("friday")
  public val saturday: Column<Boolean> = bool("saturday")
  public val sunday: Column<Boolean> = bool("sunday")
  public val startDate: Column<LocalDate> = date("start_date")
  public val endDate: Column<LocalDate> = date("end_date")

  override val primaryKey: PrimaryKey = PrimaryKey(serviceId)

  public val Mapper: (ResultRow) -> Calendar = {
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

  public fun insert(calendar: Calendar): InsertStatement<Number> = insert {
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
