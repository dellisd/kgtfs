package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.StopMapper
import io.github.dellisd.kgtfs.domain.model.Stop
import io.github.dellisd.kgtfs.domain.model.StopId
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class StopDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Stop> =
        database.stopQueries.getAll(StopMapper).executeAsList()

    public fun getById(id: String): Stop = getById(StopId(id))

    public fun getById(id: StopId): Stop = database.stopQueries.getById(id, StopMapper).executeAsOne()

    public fun getByIdOrNull(id: String): Stop? = getByIdOrNull(StopId(id))

    public fun getByIdOrNull(id: StopId): Stop? = database.stopQueries.getById(id, StopMapper).executeAsOneOrNull()

    public fun getByCode(code: String): List<Stop> = database.stopQueries.getByCode(code, StopMapper).executeAsList()
}
