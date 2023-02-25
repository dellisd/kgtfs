package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.AgencyId
import ca.derekellis.kgtfs.db.AgencyMapper
import ca.derekellis.kgtfs.db.GtfsDatabase

public class AgencyAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun all(): List<Agency> = database.agencyQueries.getAll(AgencyMapper).executeAsList()

  @GtfsScopeDsl
  public fun byId(id: AgencyId): Agency = database.agencyQueries.getById(id, AgencyMapper).executeAsOne()

  @GtfsScopeDsl
  public fun byIdOrNull(id: AgencyId): Agency? = database.agencyQueries.getById(id, AgencyMapper).executeAsOneOrNull()
}
