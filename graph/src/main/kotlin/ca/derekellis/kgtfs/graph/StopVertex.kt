package ca.derekellis.kgtfs.graph

import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId

data class StopVertex(val id: StopId, val visit: Int, val trips: Set<TripId>)
