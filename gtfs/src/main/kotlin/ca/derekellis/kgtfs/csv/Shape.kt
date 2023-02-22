package ca.derekellis.kgtfs.csv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
public value class ShapeId(public val value: String) {
    override fun toString(): String = value
}

@Serializable
public data class Shape(
    @SerialName("shape_id") val id: ShapeId,
    @SerialName("shape_pt_lat") val latitude: Double,
    @SerialName("shape_pt_lon") val longitude: Double,
    @SerialName("shape_pt_sequence") val sequence: Int,
) : Gtfs
