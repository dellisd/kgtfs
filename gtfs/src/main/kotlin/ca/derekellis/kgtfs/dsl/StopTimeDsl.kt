package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.StopTimeMapper
import ca.derekellis.kgtfs.domain.model.StopTime
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class StopTimeDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<StopTime> = database.stopTimeQueries.getAll(StopTimeMapper).executeAsList()
}
