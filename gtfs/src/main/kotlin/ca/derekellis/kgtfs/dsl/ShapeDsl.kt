package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.ShapeMapper
import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.ShapeId
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class ShapeDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Shape> = database.shapeQueries.getAll(ShapeMapper).executeAsList()

    public fun getById(id: ShapeId): List<Shape>? =
        database.shapeQueries.getById(id, ShapeMapper).executeAsList().takeIf { it.isNotEmpty() }
}