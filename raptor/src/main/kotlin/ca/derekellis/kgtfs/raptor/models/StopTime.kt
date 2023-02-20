package ca.derekellis.kgtfs.raptor.models

import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.StopId

public data class StopTime(val stop: StopId, val arrivalTime: GtfsTime, val sequence: Int)
