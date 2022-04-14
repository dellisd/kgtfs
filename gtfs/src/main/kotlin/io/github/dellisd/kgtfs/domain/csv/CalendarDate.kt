package io.github.dellisd.kgtfs.domain.csv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CalendarDate(
    @SerialName("service_id") val serviceId: String,
    val date: String,
    @SerialName("exception_type") val exceptionType: Int
)
