package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.StopMapper

public class StopAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun all(): List<Stop> = database.stopQueries.getAll(StopMapper).executeAsList()

  @GtfsScopeDsl
  public fun byId(id: String): Stop = byId(StopId(id))

  @GtfsScopeDsl
  public fun byId(id: StopId): Stop = database.stopQueries.getById(id, StopMapper).executeAsOne()

  @GtfsScopeDsl
  public fun byIdOrNull(id: String): Stop? = byIdOrNull(StopId(id))

  @GtfsScopeDsl
  public fun byIdOrNull(id: StopId): Stop? =
    database.stopQueries.getById(id, StopMapper).executeAsOneOrNull()

  @GtfsScopeDsl
  public fun byCode(code: String): List<Stop> =
    database.stopQueries.getByCode(code, StopMapper).executeAsList()
}
