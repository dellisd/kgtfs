package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.RouteMapper

public class RouteAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun all(): List<Route> = database.routeQueries.getAll(RouteMapper).executeAsList()

  @GtfsScopeDsl
  public fun byId(id: RouteId): Route = database.routeQueries.getById(id, RouteMapper).executeAsOne()

  @GtfsScopeDsl
  public fun byId(id: String): Route = byId(RouteId(id))

  @GtfsScopeDsl
  public fun byIdOrNull(id: RouteId): Route? = database.routeQueries.getById(id, RouteMapper).executeAsOneOrNull()

  @GtfsScopeDsl
  public fun byIdOrNull(id: String): Route? = byIdOrNull(RouteId(id))
}
