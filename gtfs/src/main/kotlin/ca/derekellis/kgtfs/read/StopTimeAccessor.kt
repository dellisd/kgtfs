package ca.derekellis.kgtfs.read

import ca.derekellis.kgtfs.csv.ServiceId
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.TripId
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.StopTimeMapper

public class StopTimeAccessor internal constructor(private val database: GtfsDatabase) {
  @GtfsScopeDsl
  public fun all(): List<StopTime> = database.stopTimeQueries.getAll(StopTimeMapper).executeAsList()

  @GtfsScopeDsl
  public fun byServiceIds(serviceIds: Set<ServiceId>): List<StopTime> =
    database.stopTimeQueries.getByServiceId(serviceIds, StopTimeMapper).executeAsList()

  @GtfsScopeDsl
  public fun byStopId(stopId: StopId, serviceIds: Set<ServiceId>): List<StopTime> =
    database.stopTimeQueries.getByStopId(stopId, serviceIds, StopTimeMapper).executeAsList()

  @GtfsScopeDsl
  public fun byTripId(tripId: TripId): List<StopTime> =
    database.stopTimeQueries.getByTripId(tripId, StopTimeMapper).executeAsList()
}