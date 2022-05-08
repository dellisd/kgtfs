package io.github.dellisd.raptor.models

import io.github.dellisd.kgtfs.domain.model.StopId

public data class StopTime(val stop: StopId, val arrivalTime: GtfsTime, val sequence: Int)
