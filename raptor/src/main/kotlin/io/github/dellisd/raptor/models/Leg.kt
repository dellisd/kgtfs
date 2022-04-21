package io.github.dellisd.raptor.models

import io.github.dellisd.kgtfs.domain.model.StopId
import io.github.dellisd.kgtfs.domain.model.TripId
import java.time.Duration

sealed class Leg(open val from: StopId, open val to: StopId)

data class TransferLeg(
    override val from: StopId,
    override val to: StopId,
    val duration: Duration,
    val distance: Double
) : Leg(from, to)

data class RouteLeg(
    override val from: StopId,
    override val to: StopId,
    val trip: TripId
) : Leg(from, to)
