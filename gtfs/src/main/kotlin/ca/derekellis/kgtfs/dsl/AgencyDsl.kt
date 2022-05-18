package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.AgencyMapper
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.domain.model.Agency
import ca.derekellis.kgtfs.domain.model.AgencyId
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class AgencyDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<Agency> = database.agencyQueries.getAll(AgencyMapper).executeAsList()

    public fun getById(id: AgencyId): Agency? = database.agencyQueries.getById(id, AgencyMapper).executeAsOneOrNull()
}
