package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.TripId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

public object StopTimes : Table() {
  public val tripId: Column<String> = text("trip_id")
  public val arrivalTime: Column<String> = text("arrival_time")
  public val departureTime: Column<String> = text("departure_time")
  public val stopId: Column<String> = text("stop_id")
  public val stopSequence: Column<Int> = integer("stop_sequence")
  public val pickupType: Column<Int?> = integer("pickup_type").nullable()
  public val dropOffType: Column<Int?> = integer("drop_off_time").nullable()

  private val stopTimeIndex = index(isUnique = false, tripId, stopSequence)

  public val Mapper: (ResultRow) -> StopTime = {
    StopTime(
      it[tripId].let(::TripId),
      it[arrivalTime].let(::GtfsTime),
      it[departureTime].let(::GtfsTime),
      it[stopId].let(::StopId),
      it[stopSequence],
      pickupType = it[pickupType],
      dropOffType = it[dropOffType]
    )
  }

  public fun insert(stopTime: StopTime): InsertStatement<Number> = insert {
    it[tripId] = stopTime.tripId.value
    it[arrivalTime] = stopTime.arrivalTime.toString()
    it[departureTime] = stopTime.departureTime.toString()
    it[stopId] = stopTime.stopId.value
    it[stopSequence] = stopTime.stopSequence
    it[pickupType] = stopTime.pickupType
    it[dropOffType] = stopTime.dropOffType
  }
}
