package io.github.dellisd.kgtfs.domain.csv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Route(
    @SerialName("route_id") val id: String,
    @SerialName("route_short_name") val shortName: String?,
    @SerialName("route_long_name") val longName: String?,
    @SerialName("route_desc") val desc: String?,
    @SerialName("route_type") val type: Int,
    @SerialName("route_url") val url: String?,
    @SerialName("route_color") val color: String?,
    @SerialName("route_text_color") val textColor: String?
)
