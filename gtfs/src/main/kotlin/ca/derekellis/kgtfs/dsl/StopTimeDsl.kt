package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.StopTimeMapper
import ca.derekellis.kgtfs.domain.model.ServiceId
import ca.derekellis.kgtfs.domain.model.StopId
import ca.derekellis.kgtfs.domain.model.StopTime
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class StopTimeDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<StopTime> = database.stopTimeQueries.getAll(StopTimeMapper).executeAsList()

    public fun getByServiceId(serviceIds: Set<ServiceId>): List<StopTime> =
        database.stopTimeQueries.getByServiceId(serviceIds, StopTimeMapper).executeAsList()

    public fun getByStopId(stopId: StopId, serviceIds: Set<ServiceId>): List<StopTime> =
        database.stopTimeQueries.getByStopId(stopId, serviceIds, StopTimeMapper).executeAsList()
}
