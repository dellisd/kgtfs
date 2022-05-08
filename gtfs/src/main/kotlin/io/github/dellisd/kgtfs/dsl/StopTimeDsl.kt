package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.StopTimeMapper
import io.github.dellisd.kgtfs.domain.model.StopTime
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class StopTimeDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<StopTime> = database.stopTimeQueries.getAll(StopTimeMapper).executeAsList()
}
