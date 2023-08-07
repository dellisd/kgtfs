package ca.derekellis.kgtfs.graph

import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph

private data class IntermediateStopVertex(val id: StopId, val visit: Int)

fun buildGraph(trips: Map<TripId, List<StopId>>): DirectedAcyclicGraph<StopVertex, DefaultEdge> {
  val intermediateGraph = DirectedAcyclicGraph<IntermediateStopVertex, DefaultEdge>(DefaultEdge::class.java)
  val vertexTrips = mutableMapOf<IntermediateStopVertex, MutableSet<TripId>>()

  trips.forEach { (trip, stops) ->
    val visits = mutableMapOf<StopId, Int>()

    // Add initial stop
    visits[stops.first()] = 0
    val vertex = IntermediateStopVertex(stops.first(), 0)
    intermediateGraph.addVertex(vertex)
    vertexTrips.getOrPut(vertex) { mutableSetOf() } += trip

    // Add remaining stops with edges from the previous stop
    stops.zipWithNext().forEach { (first, second) ->
      val visit = when (val visit = visits[second]) {
        null -> {
          visits[second] = 0; 0
        }

        else -> {
          visits[second] = visit + 1; visit + 1
        }
      }

      val newVertex = IntermediateStopVertex(second, visit)
      intermediateGraph.addVertex(newVertex)
      intermediateGraph.addEdge(IntermediateStopVertex(first, visits.getValue(first)), newVertex)
      vertexTrips.getOrPut(newVertex) { mutableSetOf() } += trip
    }
  }

  val graph = DirectedAcyclicGraph<StopVertex, DefaultEdge>(DefaultEdge::class.java)

  intermediateGraph.vertexSet().forEach { vertex ->
    graph.addVertex(StopVertex(vertex.id, vertex.visit, vertexTrips.getValue(vertex)))
  }
  intermediateGraph.edgeSet().forEach { edge ->
    val source = intermediateGraph.getEdgeSource(edge)
    val target = intermediateGraph.getEdgeTarget(edge)

    graph.addEdge(
      StopVertex(source.id, source.visit, vertexTrips.getValue(source)),
      StopVertex(target.id, target.visit, vertexTrips.getValue(target))
    )
  }

  return graph
}
