package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.RouteMapper
import io.github.dellisd.kgtfs.domain.model.Route
import io.github.dellisd.kgtfs.domain.model.RouteId
import me.tatarka.inject.annotations.Inject

@Inject
public class RouteDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Route> = database.routeQueries.getAll(RouteMapper).executeAsList()

    public fun getById(id: RouteId): Route? = database.routeQueries.getById(id, RouteMapper).executeAsOneOrNull()

    public fun getById(id: String): Route? = getById(RouteId(id))
}
