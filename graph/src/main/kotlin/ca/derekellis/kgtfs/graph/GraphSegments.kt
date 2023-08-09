package ca.derekellis.kgtfs.graph

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DirectedAcyclicGraph
import org.jgrapht.traverse.DepthFirstIterator

/**
 * Break a transit network [graph] into its different segments.
 * A segment of the graph is defined as a sequence of stops that are serviced by the same set of trips.
 *
 * Examples, where `X(1,2)` represents stop `X` served by trips `1` and `2`:
 * ```
 * Straight line:
 *   A(1,2) --> B(1,2) --> C(1,2)
 * has one segment: [ABC(1,2)]
 * ```
 * ```
 * Diverging branches:
 *   A(1,2) --> B(1) --> C(1)
 *          └-> D(2) --> E(2)
 * has three segments: [A(1,2), BC(1), DE(2)]
 * ```
 * ```
 * Converging branches:
 *   A(1,2) --> B(1) --> C(1) --> F(1,2)
 *          └-> D(2) --> E(2) -┘
 * has four segments: [A(1,2), BC(1), DE(2), F(1,2)]
 * ```
 * ```
 * Partial overlap:
 *   A(1,2) --> B(1,2) --> C(1) --> D(1)
 * has two segments: [AB(1,2), CD(1)]
 * ```
 */
fun segments(graph: DirectedAcyclicGraph<StopVertex, DefaultEdge>): List<List<StopVertex>> {
  val iterator = DepthFirstIterator(graph)
  val segments = mutableListOf<List<StopVertex>>()

  var currentSegment = mutableListOf<StopVertex>()

  fun endSegment() {
    segments += currentSegment
    currentSegment = mutableListOf()
  }

  iterator.forEach { vertex ->
    currentSegment += vertex

    val outgoing = graph.outgoingEdgesOf(vertex).size

    if (outgoing == 0 || outgoing >= 2) {
      // Segment either terminates or diverges, so we end the current segment and start a new one
      endSegment()
    } else {
      val outgoingEdge = graph.outgoingEdgesOf(vertex).single()
      val next = graph.getEdgeTarget(outgoingEdge)

      val incoming = graph.incomingEdgesOf(next).size
      if (incoming > 1) {
        // This segment converges with another, so we end the current segment
        endSegment()
      } else if (next.trips != vertex.trips) {
        // The next vertex has a different set of trips, this segment ends
        endSegment()
      }
    }
  }

  return segments
}
