package io.github.dellisd.raptor.models

import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.TripId
import io.github.dellisd.spatialk.geojson.Feature
import java.time.Duration

public sealed class Leg(public open val from: StopId, public open val to: StopId)

public data class TransferLeg(
    override val from: StopId,
    override val to: StopId,
    val duration: Duration,
    val distance: Double,
    val geometry: Feature?,
) : Leg(from, to)

public data class RouteLeg(
    override val from: StopId,
    override val to: StopId,
    val trip: TripId,
) : Leg(from, to)
