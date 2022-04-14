package io.github.dellisd.kgtfs.db

import app.cash.sqldelight.ColumnAdapter
import io.github.dellisd.kgtfs.domain.model.Route
import io.github.dellisd.kgtfs.domain.model.RouteId
import io.github.dellisd.kgtfs.domain.model.ServiceId
import io.github.dellisd.kgtfs.domain.model.Stop
import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.TripId
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal object InstantColumnAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.ofEpochSecond(databaseValue)

    override fun encode(value: Instant): Long = value.epochSecond
}

internal object StopIdAdapter : ColumnAdapter<StopId, String> {
    override fun decode(databaseValue: String): StopId = StopId(databaseValue)

    override fun encode(value: StopId): String = value.value
}

internal object RouteIdAdapter : ColumnAdapter<RouteId, String> {
    override fun decode(databaseValue: String): RouteId = RouteId(databaseValue)

    override fun encode(value: RouteId): String = value.value
}

internal object TripIdAdapter : ColumnAdapter<TripId, String> {
    override fun decode(databaseValue: String): TripId = TripId(databaseValue)

    override fun encode(value: TripId): String = value.value
}

internal object ServiceIdAdapter : ColumnAdapter<ServiceId, String> {
    override fun decode(databaseValue: String): ServiceId = ServiceId(databaseValue)

    override fun encode(value: ServiceId): String = value.value
}

internal object LocationTypeAdapter : ColumnAdapter<Stop.LocationType, Long> {
    override fun decode(databaseValue: Long): Stop.LocationType = Stop.LocationType.values()[databaseValue.toInt()]

    override fun encode(value: Stop.LocationType): Long = value.ordinal.toLong()
}

internal object RouteTypeAdapter : ColumnAdapter<Route.Type, Long> {
    override fun decode(databaseValue: Long): Route.Type = Route.Type.valueMap.getValue(databaseValue.toInt())

    override fun encode(value: Route.Type): Long = value.value.toLong()
}

internal object LocalDateAdapter : ColumnAdapter<LocalDate, String> {
    private val pattern = DateTimeFormatter.ofPattern("yyyyMMdd")

    override fun decode(databaseValue: String): LocalDate = LocalDate.parse(databaseValue, pattern)

    override fun encode(value: LocalDate): String = value.format(pattern)
}
