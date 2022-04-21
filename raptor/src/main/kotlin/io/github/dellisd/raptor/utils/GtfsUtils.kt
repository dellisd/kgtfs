package io.github.dellisd.raptor.utils

import io.github.dellisd.kgtfs.domain.model.RouteId
import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.StopTime
import io.github.dellisd.kgtfs.domain.model.Trip
import io.github.dellisd.kgtfs.domain.model.TripId
import java.security.MessageDigest
import java.util.PriorityQueue

private val digest = MessageDigest.getInstance("SHA-256")

fun uniqueTripSequences(
    stopTimes: List<StopTime>,
    trips: Map<TripId, Trip>
): List<TripSequence<Map<TripId, List<StopTime>>>> {
    // Make sure each trip's stop times are ordered by stop sequence
    val orderedTimes = mutableMapOf<TripId, PriorityQueue<StopTime>>()
    stopTimes.forEach { time ->
        if (time.tripId !in trips.keys) return@forEach
        orderedTimes.getOrPut(time.tripId) { PriorityQueue(26) { a, b -> a.stopSequence - b.stopSequence } }
            .add(time)
    }

    val unique = mutableMapOf<String, TripSequence<MutableMap<TripId, List<StopTime>>>>()

    orderedTimes.forEach { (tripId, times) ->
        val hashBytes = digest.digest(times.joinToString("") { it.stopId.value }.encodeToByteArray())
        val hash = hashBytes.joinToString("") { "%02x".format(it) }
        val trip = trips.getValue(tripId)

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

data class TripSequence<out M : Map<TripId, List<StopTime>>>(
    val route: RouteId,
    val sequence: List<StopId>,
    val trips: M
)
