package ca.derekellis.kgtfs.csv

public data class StopTime(
  val tripId: TripId,
  val arrivalTime: GtfsTime,
  val departureTime: GtfsTime,
  val stopId: StopId,
  val stopSequence: Int,
  val stopHeadsign: String? = null,
  val pickupType: Int? = null,
  val dropOffType: Int? = null,
  val continuousPickup: Int? = null,
  val continuousDropOff: Int? = null,
  val shapeDistTraveled: Double? = null,
  val timepoint: Int? = null,
) : Gtfs
