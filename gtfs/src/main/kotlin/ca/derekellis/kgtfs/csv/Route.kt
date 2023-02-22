package ca.derekellis.kgtfs.csv

import ca.derekellis.kgtfs.domain.serial.RouteTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
public value class RouteId(public val value: String) {
    override fun toString(): String = value
}

@Serializable
public data class Route(
  @SerialName("route_id") val id: RouteId,
  @SerialName("route_short_name") val shortName: String?,
  @SerialName("route_long_name") val longName: String?,
  @SerialName("route_desc") val desc: String?,
  @SerialName("route_type") val type: Type,
  @SerialName("route_url") val url: String? = null,
  @SerialName("route_color") val color: String? = null,
  @SerialName("route_text_color") val textColor: String? = null
) : Gtfs {
    @Serializable(with = RouteTypeSerializer::class)
    public enum class Type(internal val value: Int) {
        Tram(0),
        Subway(1),
        Rail(2),
        Bus(3),
        Ferry(4),
        CableTram(5),
        AerialLift(6),
        Funicular(7),
        Trolleybus(11),
        Monorail(12);

        public companion object {
            internal val valueMap = mapOf(
                Tram.value to Tram,
                Subway.value to Subway,
                Rail.value to Rail,
                Bus.value to Bus,
                Ferry.value to Ferry,
                CableTram.value to CableTram,
                AerialLift.value to AerialLift,
                Funicular.value to Funicular,
                Trolleybus.value to Trolleybus,
                Monorail.value to Monorail
            )
        }
    }
}
