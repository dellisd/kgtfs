package io.github.dellisd.kgtfs.domain.csv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Stop(
    @SerialName("stop_id") val id: String,
    @SerialName("stop_code") val code: String? = null,
    @SerialName("stop_name") val name: String? = null,
    @SerialName("stop_desc") val description: String? = null,
    @SerialName("stop_lat") val latitude: Double? = null,
    @SerialName("stop_lon") val longitude: Double? = null,
    @SerialName("zone_id") val zoneId: String? = null,
    @SerialName("stop_url") val url: String? = null,
    @SerialName("location_type") val locationType: Int? = null,
    @SerialName("parent_station") val parentStation: String? = null,
    @SerialName("stop_timezone") val timezone: String? = null,
    @SerialName("wheelchair_boarding") val wheelchairBoarding: Long? = null,
    @SerialName("level_id") val levelId: String? = null,
    @SerialName("platform_code") val platformCode: String? = null
)
