package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.TripMapper
import ca.derekellis.kgtfs.domain.model.ServiceId
import ca.derekellis.kgtfs.domain.model.Trip
import ca.derekellis.kgtfs.domain.model.TripId
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class TripDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Trip> = database.tripQueries.getAll(TripMapper).executeAsList()

    public fun getById(id: TripId): Trip = database.tripQueries.getById(id, TripMapper).executeAsOne()

    public fun getByIdOrNull(id: TripId): Trip? = database.tripQueries.getById(id, TripMapper).executeAsOneOrNull()

    public fun getByServiceId(serviceIds: Set<ServiceId>): List<Trip> =
        database.tripQueries.getByServiceId(serviceIds, TripMapper).executeAsList()
}
