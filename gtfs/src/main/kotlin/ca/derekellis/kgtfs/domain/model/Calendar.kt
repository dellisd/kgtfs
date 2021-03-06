@file:UseSerializers(DateSerializer::class)

package ca.derekellis.kgtfs.domain.model

import ca.derekellis.kgtfs.domain.serial.DateSerializer
import ca.derekellis.kgtfs.domain.serial.IntBooleanSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate

@JvmInline
@Serializable
public value class ServiceId(public val value: String) {
    override fun toString(): String = value
}

@Serializable
public data class Calendar(
    @SerialName("service_id") val serviceId: ServiceId,
    @Serializable(with = IntBooleanSerializer::class) val monday: Boolean,
    @Serializable(with = IntBooleanSerializer::class) val tuesday: Boolean,
    @Serializable(with = IntBooleanSerializer::class) val wednesday: Boolean,
    @Serializable(with = IntBooleanSerializer::class) val thursday: Boolean,
    @Serializable(with = IntBooleanSerializer::class) val friday: Boolean,
    @Serializable(with = IntBooleanSerializer::class) val saturday: Boolean,
    @Serializable(with = IntBooleanSerializer::class) val sunday: Boolean,
    @SerialName("start_date") val startDate: LocalDate,
    @SerialName("end_date") val endDate: LocalDate
)
