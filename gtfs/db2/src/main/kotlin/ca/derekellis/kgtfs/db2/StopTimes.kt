package ca.derekellis.kgtfs.db2

import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.TripId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert

object StopTimes : Table() {
  val tripId: Column<String> = text("trip_id")
  val arrivalTime: Column<String> = text("arrival_time")
  val departureTime: Column<String> = text("departure_time")
  val stopId: Column<String> = text("stop_id")
  val stopSequence: Column<Int> = integer("stop_sequence")
  val pickupType: Column<Int?> = integer("pickup_type").nullable()
  val dropOffType: Column<Int?> = integer("drop_off_time").nullable()

  private val stopTimeIndex = index(isUnique = false, tripId, stopSequence)

  val Mapper: (ResultRow) -> StopTime = {
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

  fun insert(stopTime: StopTime) = insert {
    it[tripId] = stopTime.tripId.value
    it[arrivalTime] = stopTime.arrivalTime.toString()
    it[departureTime] = stopTime.departureTime.toString()
    it[stopId] = stopTime.stopId.value
    it[stopSequence] = stopTime.stopSequence
    it[pickupType] = stopTime.pickupType
    it[dropOffType] = stopTime.dropOffType
  }
}
