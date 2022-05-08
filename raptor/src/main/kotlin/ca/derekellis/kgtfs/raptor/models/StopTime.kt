package ca.derekellis.kgtfs.raptor.models

import ca.derekellis.kgtfs.domain.model.StopId

public data class StopTime(val stop: StopId, val arrivalTime: GtfsTime, val sequence: Int)
