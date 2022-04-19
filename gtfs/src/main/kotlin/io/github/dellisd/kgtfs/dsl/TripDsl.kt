package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.TripMapper
import io.github.dellisd.kgtfs.domain.model.Trip
import io.github.dellisd.kgtfs.domain.model.TripId
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class TripDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Trip> = database.tripQueries.getAll(TripMapper).executeAsList()

    public fun getById(id: TripId): Trip = database.tripQueries.getById(id, TripMapper).executeAsOne()

    public fun getByIdOrNull(id: TripId): Trip? = database.tripQueries.getById(id, TripMapper).executeAsOneOrNull()
}
