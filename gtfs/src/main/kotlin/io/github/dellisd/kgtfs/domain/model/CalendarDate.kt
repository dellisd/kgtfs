package io.github.dellisd.kgtfs.domain.model

import io.github.dellisd.kgtfs.domain.serial.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
public data class CalendarDate(
    @SerialName("service_id") val serviceId: ServiceId,
    @Serializable(with = DateSerializer::class) val date: LocalDate,
    @SerialName("exception_type") val exceptionType: Int
)
