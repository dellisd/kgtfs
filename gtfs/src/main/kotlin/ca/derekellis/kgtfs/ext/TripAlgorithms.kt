package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.domain.model.GtfsTime
import ca.derekellis.kgtfs.domain.model.RouteId
import ca.derekellis.kgtfs.domain.model.ServiceId
import ca.derekellis.kgtfs.domain.model.StopId
import ca.derekellis.kgtfs.domain.model.StopTime
import ca.derekellis.kgtfs.domain.model.TripId
import ca.derekellis.kgtfs.dsl.StaticGtfsScope
import java.security.MessageDigest
import java.time.Duration
import java.time.LocalDate
import java.util.PriorityQueue

public fun StaticGtfsScope.uniqueTripSequences(date: LocalDate = LocalDate.now()): List<TripSequence<Map<TripId, List<StopTime>>>> {
    val serviceIds = calendar.onDate(date).map { it.serviceId }.toSet()
    return uniqueTripSequences(serviceIds)
}

public fun StaticGtfsScope.uniqueTripSequences(serviceIds: Set<ServiceId>): List<TripSequence<Map<TripId, List<StopTime>>>> {
    val digest = MessageDigest.getInstance("SHA-256")
    val times = stopTimes.getByServiceId(serviceIds)
    val tripMap = trips.getByServiceId(serviceIds).associateBy { it.id }

    // Make sure each trip's stop times are ordered by stop sequence
    val orderedTimes = mutableMapOf<TripId, PriorityQueue<StopTime>>()
    times.forEach { time ->
        if (time.tripId !in tripMap.keys) return@forEach
        orderedTimes.getOrPut(time.tripId) { PriorityQueue(26) { a, b -> a.stopSequence - b.stopSequence } }
            .add(time)
    }

    val unique = mutableMapOf<String, TripSequence<MutableMap<TripId, List<StopTime>>>>()

    orderedTimes.forEach { (tripId, times) ->
        val hashBytes = digest.digest(times.joinToString("") { it.stopId.value }.encodeToByteArray())
        val hash = hashBytes.joinToString("") { "%02x".format(it) }
        val trip = tripMap.getValue(tripId)

        // Create a new id to represent this sequence
        val newId = "${trip.routeId}-${trip.directionId}#${hash}"
        unique.getOrPut(newId) {
            TripSequence(
                RouteId(newId),
                times.map { it.stopId },
                mutableMapOf()
            )
        }.trips[tripId] =
            times.toList()
    }

    return unique.values.toList()
}

public data class TripSequence<out M : Map<TripId, List<StopTime>>>(
    val route: RouteId,
    val sequence: List<StopId>,
    val trips: M
)

public fun TripSequence<*>.frequency(from: GtfsTime, until: GtfsTime): Duration? {
    check(until > from) { "'until' must be after 'from'" }

    val diffs = trips.values
        .asSequence()
        .map { it.first() }
        .filter { it.arrivalTime in from..until }
        .zipWithNext()
        .map { (a, b) -> b.arrivalTime - a.arrivalTime }
        .filter { it.toSeconds() > 0 }
        .toList()

    return if (diffs.isNotEmpty()) {
        Duration.ofSeconds(diffs.sumOf { it.toSeconds() } / diffs.size)
    } else {
        null
    }
}
