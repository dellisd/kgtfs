package ca.derekellis.kgtfs.raptor.models

import ca.derekellis.kgtfs.csv.StopId
import kotlinx.serialization.json.JsonObject
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.LineString

/**
 * Represents a transfer between stops
 *
 * @property from The starting stop for this transfer
 * @property to The destination stop for this transfer
 * @property distance The walking distance between [from] and [to] in metres
 * @property geometry A [Feature] representing the path taken in this transfer (e.g. a `LineString`)
 */
public data class Transfer(val from: StopId, val to: StopId, val distance: Double, val geometry: Feature<LineString, JsonObject>?)
