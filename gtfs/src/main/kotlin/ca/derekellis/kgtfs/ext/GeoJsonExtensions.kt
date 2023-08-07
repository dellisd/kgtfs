package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.Stop
import io.github.dellisd.spatialk.geojson.LineString
import io.github.dellisd.spatialk.geojson.Position
import io.github.dellisd.spatialk.geojson.dsl.lineString
import io.github.dellisd.spatialk.geojson.dsl.lngLat
import io.github.dellisd.spatialk.turf.ExperimentalTurfApi
import io.github.dellisd.spatialk.turf.lineSlice

public fun List<Shape>.lineString(): LineString = lineString {
  forEach { point(it.longitude, it.latitude) }
}

@OptIn(ExperimentalTurfApi::class)
public fun List<Shape>.lineStringBetween(a: Stop, b: Stop): LineString {
  return lineSlice(a.point, b.point, lineString())
}

public val Stop.point: Position
  get() =
    if (longitude == null || latitude == null) {
      throw IllegalArgumentException("Stop longitude and latitude can not be null")
    } else {
      lngLat(longitude, latitude)
    }
