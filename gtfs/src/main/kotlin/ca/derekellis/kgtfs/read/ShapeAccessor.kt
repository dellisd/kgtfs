package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.ShapeId
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.ShapeMapper

public class ShapeAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun all(): List<Shape> = database.shapeQueries.getAll(ShapeMapper).executeAsList()

  @GtfsScopeDsl
  public fun byId(id: ShapeId): List<Shape>? =
    database.shapeQueries.getById(id, ShapeMapper).executeAsList().takeIf { it.isNotEmpty() }
}