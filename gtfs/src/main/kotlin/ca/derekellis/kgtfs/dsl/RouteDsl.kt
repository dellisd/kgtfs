package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.RouteMapper
import ca.derekellis.kgtfs.domain.model.Route
import ca.derekellis.kgtfs.domain.model.RouteId
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class RouteDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Route> = database.routeQueries.getAll(RouteMapper).executeAsList()

    public fun getById(id: RouteId): Route? = database.routeQueries.getById(id, RouteMapper).executeAsOneOrNull()

    public fun getById(id: String): Route? = getById(RouteId(id))
}
