package io.github.dellisd.kgtfs.domain.csv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Trip(
    @SerialName("route_id") val routeId: String,
    @SerialName("service_id") val serviceId: String,
    @SerialName("trip_id") val id: String,
    @SerialName("trip_headsign") val headsign: String? = null,
    @SerialName("trip_short_name") val shortName: String? = null,
    @SerialName("direction_id") val directionId: Int? = null,
    @SerialName("block_id") val blockId: String? = null,
    @SerialName("shape_id") val shapeId: String? = null,
    @SerialName("wheelchair_accessible") val wheelchairAccessible: Int? = null,
    @SerialName("bikes_allowed") val bikesAllowed: Int? = null
)
