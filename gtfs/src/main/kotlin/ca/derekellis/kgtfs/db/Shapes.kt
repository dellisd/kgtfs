package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.ShapeId
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.InsertStatement

public object Shapes : Table(name = "Shape") {
  public val id: Column<String> = text("shape_id")
  public val pointLatitude: Column<Double> = double("shape_pt_lat")
  public val pointLongitude: Column<Double> = double("shape_pt_lon")
  public val pointSequence: Column<Int> = integer("shape_pt_sequence")

  private val shapeIndex = index(isUnique = false, id, pointSequence)

  public val Mapper: (ResultRow) -> Shape = {
    Shape(it[id].let(::ShapeId), it[pointLatitude], it[pointLongitude], it[pointSequence])
  }

  public fun insert(shape: Shape): InsertStatement<Number> = insert {
    it[id] = shape.id.value
    it[pointLatitude] = shape.latitude
    it[pointLongitude] = shape.longitude
    it[pointSequence] = shape.sequence
  }
}
