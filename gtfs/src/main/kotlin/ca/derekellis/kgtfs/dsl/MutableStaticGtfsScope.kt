package ca.derekellis.kgtfs.dsl

import app.cash.sqldelight.db.SqlDriver
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.Trip
import me.tatarka.inject.annotations.Inject

@Inject
public class MutableStaticGtfsScope(
    stops: StopDsl,
    calendar: CalendarDsl,
    dates: CalendarDateDsl,
    stopTimes: StopTimeDsl,
    trips: TripDsl,
    routes: RouteDsl,
    agencies: AgencyDsl,
    shapes: ShapeDsl,
    database: GtfsDatabase,
    driver: SqlDriver,
) : StaticGtfsScope(stops, calendar, dates, stopTimes, trips, routes, agencies, shapes, database, driver) {
    public fun TripDsl.add(trip: Trip, stopTimes: List<StopTime>): Unit =
        with(this@MutableStaticGtfsScope) {
            database.transaction {
                database.tripQueries.insert(
                    trip.routeId,
                    trip.serviceId,
                    trip.id,
                    trip.headsign,
                    trip.directionId,
                    trip.blockId,
                    trip.shapeId
                )

                stopTimes.forEach {
                    database.stopTimeQueries.insert(
                        it.tripId,
                        it.arrivalTime,
                        it.departureTime,
                        it.stopId,
                        it.stopSequence,
                        it.pickupType,
                        it.dropOffType
                    )
                }
            }
        }

    public fun StopDsl.add(stop: Stop): Unit = with(this@MutableStaticGtfsScope) {
        database.stopQueries.insert(
            stop.id,
            stop.code,
            stop.name,
            stop.description,
            stop.latitude,
            stop.longitude,
            stop.zoneId,
            stop.url,
            stop.locationType
        )
    }
}
