package ca.derekellis.kgtfs.csv

@JvmInline
public value class StopId(public val value: String) {
  override fun toString(): String = value
}

public data class Stop(
  val id: StopId,
  val code: String? = null,
  val name: String? = null,
  val description: String? = null,
  val latitude: Double? = null,
  val longitude: Double? = null,
  val zoneId: String? = null,
  val url: String? = null,
  val locationType: LocationType? = null,
  val parentStation: StopId? = null,
  val timezone: String? = null,
  val wheelchairBoarding: Int? = null,
  val levelId: String? = null,
  val platformCode: String? = null,
) : Gtfs {
  public enum class LocationType {
    Platform,
    Station,
    EntranceExit,
    GenericNode,
    BoardingArea,
  }
}
