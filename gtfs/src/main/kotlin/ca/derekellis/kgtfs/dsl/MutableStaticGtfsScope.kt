package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.domain.model.Stop
import ca.derekellis.kgtfs.domain.model.StopTime
import ca.derekellis.kgtfs.domain.model.Trip
import me.tatarka.inject.annotations.Inject

@Inject
public class MutableStaticGtfsScope(
    stops: StopDsl,
    calendar: CalendarDsl,
    stopTimes: StopTimeDsl,
    trips: TripDsl,
    database: GtfsDatabase
) : StaticGtfsScope(stops, calendar, stopTimes, trips, database) {
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
