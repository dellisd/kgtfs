package io.github.dellisd.kgtfs.domain.csv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Calendar(
    @SerialName("service_id") val serviceId: String,
    val monday: Int,
    val tuesday: Int,
    val wednesday: Int,
    val thursday: Int,
    val friday: Int,
    val saturday: Int,
    val sunday: Int,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String
)
