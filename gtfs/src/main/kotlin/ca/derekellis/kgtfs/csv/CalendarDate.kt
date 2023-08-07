package ca.derekellis.kgtfs.csv

import ca.derekellis.kgtfs.csv.serializers.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
public data class CalendarDate(
  @SerialName("service_id") val serviceId: ServiceId,
  @Serializable(with = DateSerializer::class) val date: LocalDate,
  @SerialName("exception_type") val exceptionType: Int,
) : Gtfs
