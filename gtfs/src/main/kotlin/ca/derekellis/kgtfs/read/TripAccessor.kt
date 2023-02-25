package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.ServiceId
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.csv.TripId
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.TripMapper

public class TripAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun all(): List<Trip> = database.tripQueries.getAll(TripMapper).executeAsList()

  @GtfsScopeDsl
  public fun byId(id: TripId): Trip = database.tripQueries.getById(id, TripMapper).executeAsOne()

  @GtfsScopeDsl
  public fun byIdOrNull(id: TripId): Trip? = database.tripQueries.getById(id, TripMapper).executeAsOneOrNull()

  @GtfsScopeDsl
  public fun byServiceId(serviceId: ServiceId): List<Trip> =
    database.tripQueries.getByServiceId(setOf(serviceId), TripMapper).executeAsList()

  @GtfsScopeDsl
  public fun byServiceIds(serviceIds: Set<ServiceId>): List<Trip> =
    database.tripQueries.getByServiceId(serviceIds, TripMapper).executeAsList()

  @GtfsScopeDsl
  public fun byCalendar(calendar: Calendar): List<Trip> = byServiceId(calendar.serviceId)

  @GtfsScopeDsl
  public fun byCalendars(calendars: Set<Calendar>): List<Trip> =
    byServiceIds(calendars.mapTo(mutableSetOf()) { it.serviceId })
}
