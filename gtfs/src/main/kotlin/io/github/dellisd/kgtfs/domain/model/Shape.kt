package io.github.dellisd.kgtfs.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Shape(
    @SerialName("shape_id") val id: TripId,
    @SerialName("shape_pt_lat") val latitude: Double,
    @SerialName("shape_pt_lon") val longitude: Double,
    @SerialName("shape_pt_sequence") val sequence: Int,
)
