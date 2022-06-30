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

public fun StaticGtfsScope.uniqueTripSequences(date: LocalDate = LocalDate.now()): List<TripSequence> {
    val serviceIds = calendar.onDate(date).map { it.serviceId }.toSet()
    return uniqueTripSequences(serviceIds)
}

public fun StaticGtfsScope.uniqueTripSequences(serviceIds: Set<ServiceId>): List<TripSequence> {
    val times = stopTimes.getByServiceId(serviceIds)
    val tripMap = trips.getByServiceId(serviceIds).associateBy { it.id }

    // Make sure each trip's stop times are ordered by stop sequence
    val orderedTimes = mutableMapOf<TripId, PriorityQueue<StopTime>>()
    times.forEach { time ->
        if (time.tripId !in tripMap.keys) return@forEach
        orderedTimes.getOrPut(time.tripId) { PriorityQueue(26) { a, b -> a.stopSequence - b.stopSequence } }
            .add(time)
    }

    val unique = mutableMapOf<String, TripSequence>()

    orderedTimes.forEach { (tripId, times) ->
        val hash = sequenceHashOf(tripId)
        val trip = tripMap.getValue(tripId)

        // Create a new id to represent this sequence
        val newId = "${trip.routeId}-${trip.directionId}#${hash}"
        unique.getOrPut(newId) {
            TripSequence(
                RouteId(newId),
                trip.routeId,
                times.map { it.stopId },
                mutableMapOf(),
                hash
            )
        }
            ._trips[tripId] = times.toList()
    }

    return unique.values.toList()
}

public fun StaticGtfsScope.sequenceHashOf(trip: TripId): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = trips.getById(trip).stopTimes
        .joinToString("") { it.stopId.value }
        .encodeToByteArray()

    return digest.digest(bytes).joinToString("") { "%02x".format(it) }
}

/**
 * Represents a unique sequence of stops in a route and the corresponding trips for this sequence.
 *
 * @param uniqueId A unique identifier for this sequence. The format is: "`${gtfsId}-${directionId}-${hash}`"
 * where the `directionId` is taken from the GTFS trip entries, and the `hash` is the value of [hash].
 * @param gtfsId The GTFS ID of the Route that this sequence is for
 * @param sequence The sequence of stops
 * @param hash A unique hash of the sequence of stops, useful for indexing
 */
public data class TripSequence(
    val uniqueId: RouteId,
    val gtfsId: RouteId,
    val sequence: List<StopId>,
    internal val _trips: MutableMap<TripId, List<StopTime>>,
    val hash: String,
) {
    /**
     * All trips and their corresponding stop times that follow this sequence on this particular route.
     */
    val trips: Map<TripId, List<StopTime>> get() = _trips
}

public fun TripSequence.frequency(from: GtfsTime, until: GtfsTime): Duration? {
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
