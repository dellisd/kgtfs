package io.github.dellisd.raptor.db

import app.cash.sqldelight.ColumnAdapter
import io.github.dellisd.kgtfs.domain.model.RouteId
import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.TripId
import io.github.dellisd.raptor.models.GtfsTime
import io.github.dellisd.spatialk.geojson.Feature

internal object RouteIdAdapter : ColumnAdapter<RouteId, String> {
    override fun decode(databaseValue: String): RouteId = RouteId(databaseValue)

    override fun encode(value: RouteId): String = value.value
}

internal object StopIdAdapter : ColumnAdapter<StopId, String> {
    override fun decode(databaseValue: String): StopId = StopId(databaseValue)

    override fun encode(value: StopId): String = value.value
}

internal object TripIdAdapter : ColumnAdapter<TripId, String> {
    override fun decode(databaseValue: String): TripId = TripId(databaseValue)

    override fun encode(value: TripId): String = value.value
}

internal object GtfsTimeAdapter : ColumnAdapter<GtfsTime, String> {
    override fun decode(databaseValue: String): GtfsTime = GtfsTime(databaseValue)

    override fun encode(value: GtfsTime): String =
        "${value.hour.toString().padStart(2, '0')}:${value.minute.toString().padStart(2, '0')}:${
            value.second.toString().padStart(2, '0')
        }"
}

internal object FeatureAdapter : ColumnAdapter<Feature, String> {
    override fun decode(databaseValue: String): Feature = Feature.fromJson(databaseValue)

    override fun encode(value: Feature): String = value.json()
}
