package ca.derekellis.kgtfs.graph

import ca.derekellis.kgtfs.csv.TripId
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SegmentsTest {

  @Test
  fun `straight line is one segment`() {
    val tripId = TripId("Trip")
    val stops = listOfStops("A", "B", "C", "D")

    val graph = buildGraph(
      mapOf(tripId to stops),
    )
    val segments = segments(graph)

    assertThat(segments).hasSize(1)
    assertThat(segments.single())
      .containsExactlyElementsIn(stops.map { StopVertex(it, 0, setOf(tripId)) })
      .inOrder()
  }

  @Test
  fun `diverging graph is three segments`() {
    val graph = buildGraph(
      mapOf(
        TripId("TripA") to listOfStops("A", "B", "C", "D"),
        TripId("TripB") to listOfStops("A", "B", "E", "F"),
      ),
    )

    val segments = segments(graph)

    assertThat(segments).hasSize(3)
    assertThat(segments).containsExactly(
      listOfStops("A", "B").map { StopVertex(it, 0, setOf(TripId("TripA"), TripId("TripB"))) },
      listOfStops("C", "D").map { StopVertex(it, 0, setOf(TripId("TripA"))) },
      listOfStops("E", "F").map { StopVertex(it, 0, setOf(TripId("TripB"))) },
    )
  }

  @Test
  fun `converging branch is four segments`() {
    val graph = buildGraph(
      mapOf(
        TripId("TripA") to listOfStops("A", "B", "C", "D", "E", "F"),
        TripId("TripB") to listOfStops("A", "B", "G", "H", "E", "F"),
      ),
    )

    val segments = segments(graph)

    assertThat(segments).hasSize(4)
    assertThat(segments).containsExactly(
      listOfStops("A", "B").map { StopVertex(it, 0, setOf(TripId("TripA"), TripId("TripB"))) },
      listOfStops("C", "D").map { StopVertex(it, 0, setOf(TripId("TripA"))) },
      listOfStops("G", "H").map { StopVertex(it, 0, setOf(TripId("TripB"))) },
      listOfStops("E", "F").map { StopVertex(it, 0, setOf(TripId("TripA"), TripId("TripB"))) },
    )
  }

  @Test
  fun `partial overlap is two segments`() {
    val graph = buildGraph(
      mapOf(
        TripId("TripA") to listOfStops("A", "B", "C", "D"),
        TripId("TripB") to listOfStops("A", "B"),
      ),
    )

    val segments = segments(graph)

    assertThat(segments).hasSize(2)
    assertThat(segments).containsExactly(
      listOfStops("A", "B").map { StopVertex(it, 0, setOf(TripId("TripA"), TripId("TripB"))) },
      listOfStops("C", "D").map { StopVertex(it, 0, setOf(TripId("TripA"))) },
    )
  }
}
