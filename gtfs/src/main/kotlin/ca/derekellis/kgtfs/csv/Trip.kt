package ca.derekellis.kgtfs.csv

@JvmInline
public value class TripId(public val value: String) {
  override fun toString(): String = value
}

public data class Trip(
  val routeId: RouteId,
  val serviceId: ServiceId,
  val id: TripId,
  val headsign: String? = null,
  val shortName: String? = null,
  val directionId: Int? = null,
  val blockId: String? = null,
  val shapeId: ShapeId? = null,
  val wheelchairAccessible: Int? = null,
  val bikesAllowed: Boolean? = null,
) : Gtfs
