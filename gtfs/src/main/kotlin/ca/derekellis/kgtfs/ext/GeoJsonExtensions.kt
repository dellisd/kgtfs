package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.Stop
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.geojson.dsl.buildLineString
import org.maplibre.spatialk.turf.misc.slice

public fun List<Shape>.lineString(): LineString = buildLineString {
  forEach { add(it.longitude, it.latitude) }
}

public fun List<Shape>.lineStringBetween(a: Stop, b: Stop): LineString {
  return lineString().slice(a.point, b.point)
}

public val Stop.point: Position
  get() =
    if (longitude == null || latitude == null) {
      throw IllegalArgumentException("Stop longitude and latitude can not be null")
    } else {
      Position(longitude, latitude)
    }
