package ca.derekellis.kgtfs.graph

import ca.derekellis.kgtfs.csv.StopId

fun listOfStops(vararg stopIds: String) = stopIds.map(::StopId)
