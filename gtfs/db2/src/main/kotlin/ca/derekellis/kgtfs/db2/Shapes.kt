package ca.derekellis.kgtfs.db2

import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.ShapeId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert

object Shapes : Table(name = "Shape") {
  val id: Column<String> = text("shape_id")
  val pointLatitude: Column<Double> = double("shape_pt_lat")
  val pointLongitude: Column<Double> = double("shape_pt_lon")
  val pointSequence: Column<Int> = integer("shape_pt_sequence")

  private val shapeIndex = index(isUnique = false, id, pointSequence)

  val Mapper: (ResultRow) -> Shape = {
    Shape(it[id].let(::ShapeId), it[pointLatitude], it[pointLongitude], it[pointSequence])
  }

  fun insert(shape: Shape) = insert {
    it[id] = shape.id.value
    it[pointLatitude] = shape.latitude
    it[pointLongitude] = shape.longitude
    it[pointSequence] = shape.sequence
  }
}
