package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.Route
import io.github.dellisd.kgtfs.db.Stop
import me.tatarka.inject.annotations.Inject

@Inject
public class StopDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Stop> = database.stopQueries.getAll().executeAsList()

    public fun getById(id: String): Stop? = database.stopQueries.getById(id).executeAsOneOrNull()

    public fun getByCode(code: String): List<Stop> = database.stopQueries.getByCode(code).executeAsList()
}
