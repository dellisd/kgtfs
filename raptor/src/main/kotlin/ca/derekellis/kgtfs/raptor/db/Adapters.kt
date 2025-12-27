package ca.derekellis.kgtfs.raptor.db

import app.cash.sqldelight.ColumnAdapter
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId
import kotlinx.serialization.json.JsonObject
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.toJson

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

  override fun encode(value: GtfsTime): String = value.toString()
}

internal object LineStringFeatureAdapter : ColumnAdapter<Feature<LineString, JsonObject>, String> {
  override fun decode(databaseValue: String): Feature<LineString, JsonObject> = Feature.fromJson(databaseValue)

  override fun encode(value: Feature<LineString, JsonObject>): String = value.toJson()
}
