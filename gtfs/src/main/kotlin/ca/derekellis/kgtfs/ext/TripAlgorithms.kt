package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.GtfsDbScope
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.ServiceId
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.TripId
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import java.security.MessageDigest
import java.time.Duration
import java.time.LocalDate
import java.util.PriorityQueue

@GtfsAlgorithmsDsl
public fun GtfsDbScope.uniqueTripSequences(date: LocalDate = LocalDate.now()): List<TripSequence> {
  val serviceIds = Calendars.onDate(date).map { it.serviceId }.toSet()
  return uniqueTripSequences(serviceIds)
}

@GtfsAlgorithmsDsl
public fun GtfsDbScope.uniqueTripSequences(serviceIds: Set<ServiceId>): List<TripSequence> {
  val times = StopTimes.join(Trips, JoinType.LEFT, onColumn = StopTimes.tripId, otherColumn = Trips.id)
    .select { Trips.serviceId inList serviceIds.map { it.value } }.map(StopTimes.Mapper)
  val tripMap =
    Trips.select { Trips.serviceId inList serviceIds.map { it.value } }.map(Trips.Mapper).associateBy { it.id }

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

public fun GtfsDbScope.sequenceHashOf(trip: TripId): String {
  val digest = MessageDigest.getInstance("SHA-256")
  val bytes = Trips.join(StopTimes, JoinType.LEFT, onColumn = Trips.id, otherColumn = StopTimes.tripId)
    .select { Trips.id eq trip.value }
    .map(StopTimes.Mapper)
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
public class TripSequence(
  public val uniqueId: RouteId,
  public val gtfsId: RouteId,
  public val sequence: List<StopId>,
  internal val _trips: MutableMap<TripId, List<StopTime>>,
  public val hash: String,
) {
  /**
   * All trips and their corresponding stop times that follow this sequence on this particular route.
   */
  public val trips: Map<TripId, List<StopTime>> get() = _trips

  public operator fun component1(): RouteId = uniqueId
  public operator fun component2(): RouteId = gtfsId
  public operator fun component3(): List<StopId> = sequence
  public operator fun component4(): Map<TripId, List<StopTime>> = trips
  public operator fun component5(): String = hash

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as TripSequence

    if (uniqueId != other.uniqueId) return false
    if (gtfsId != other.gtfsId) return false
    if (sequence != other.sequence) return false
    if (hash != other.hash) return false
    if (trips != other.trips) return false

    return true
  }

  override fun hashCode(): Int {
    var result = uniqueId.hashCode()
    result = 31 * result + gtfsId.hashCode()
    result = 31 * result + sequence.hashCode()
    result = 31 * result + hash.hashCode()
    result = 31 * result + trips.hashCode()
    return result
  }

  override fun toString(): String =
    "TripSequence(uniqueId=$uniqueId, gtfsId=$gtfsId, sequence=$sequence,  trips=$trips, hash='$hash')"
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
