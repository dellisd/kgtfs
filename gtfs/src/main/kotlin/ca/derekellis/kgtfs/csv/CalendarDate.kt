package ca.derekellis.kgtfs.csv

import java.time.LocalDate

public data class CalendarDate(
  val serviceId: ServiceId,
  val date: LocalDate,
  val exceptionType: Int,
) : Gtfs
