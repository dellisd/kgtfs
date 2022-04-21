package io.github.dellisd.raptor.models

import io.github.dellisd.kgtfs.domain.model.StopId

/**
 * Represents a transfer between stops
 *
 * @property from The starting stop for this transfer
 * @property to The destination stop for this transfer
 * @property distance The walking distance between [from] and [to] in metres
 */
data class Transfer(val from: StopId, val to: StopId, val distance: Double)
