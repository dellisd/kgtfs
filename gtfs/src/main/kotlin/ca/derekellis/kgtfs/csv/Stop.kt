package ca.derekellis.kgtfs.csv

import ca.derekellis.kgtfs.csv.serializers.LocationTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
public value class StopId(public val value: String) {
  override fun toString(): String = value
}

@Serializable
public data class Stop(
  @SerialName("stop_id") val id: StopId,
  @SerialName("stop_code") val code: String? = null,
  @SerialName("stop_name") val name: String? = null,
  @SerialName("stop_desc") val description: String? = null,
  @SerialName("stop_lat") val latitude: Double? = null,
  @SerialName("stop_lon") val longitude: Double? = null,
  @SerialName("zone_id") val zoneId: String? = null,
  @SerialName("stop_url") val url: String? = null,
  @SerialName("location_type") val locationType: LocationType? = null,
  @SerialName("parent_station") val parentStation: StopId? = null,
  @SerialName("stop_timezone") val timezone: String? = null,
  @SerialName("wheelchair_boarding") val wheelchairBoarding: Int? = null,
  @SerialName("level_id") val levelId: String? = null,
  @SerialName("platform_code") val platformCode: String? = null,
) : Gtfs {
  @Serializable(with = LocationTypeSerializer::class)
  public enum class LocationType {
    Platform,
    Station,
    EntranceExit,
    GenericNode,
    BoardingArea,
  }
}
