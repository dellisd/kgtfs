package io.github.dellisd.kgtfs.domain.csv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class StopTime(
    @SerialName("trip_id") val tripId: String,
    @SerialName("arrival_time") val arrivalTime: String,
    @SerialName("departure_time") val departureTime: String,
    @SerialName("stop_id") val stopId: String,
    @SerialName("stop_sequence") val stopSequence: Int,
    @SerialName("stop_headsign") val stopHeadsign: String? = null,
    @SerialName("pickup_type") val pickupType: Int? = null,
    @SerialName("drop_off_type") val dropOffType: Int? = null,
    @SerialName("continuous_pickup") val continuousPickup: Int? = null,
    @SerialName("continuous_drop_off") val continuousDropOff: Int? = null,
    @SerialName("shape_dist_travelled") val shapeDistTraveled: Double? = null,
    @SerialName("timepoint") val timepoint: Int? = null
)
