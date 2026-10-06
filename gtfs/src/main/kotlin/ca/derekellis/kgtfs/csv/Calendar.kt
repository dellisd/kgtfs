package ca.derekellis.kgtfs.csv

import java.time.LocalDate

@JvmInline
public value class ServiceId(public val value: String) {
  override fun toString(): String = value
}

public data class Calendar(
  val serviceId: ServiceId,
  val monday: Boolean,
  val tuesday: Boolean,
  val wednesday: Boolean,
  val thursday: Boolean,
  val friday: Boolean,
  val saturday: Boolean,
  val sunday: Boolean,
  val startDate: LocalDate,
  val endDate: LocalDate,
) : Gtfs
