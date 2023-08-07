package ca.derekellis.kgtfs.graph

import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.TripId
import com.google.common.truth.Truth.assertThat
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.nio.DefaultAttribute
import org.jgrapht.nio.dot.DOTExporter
import org.junit.Test
import java.io.StringWriter

class BuildGraphTest {

  @Test
  fun `basic graph`() {
    val result = buildGraph(
      mapOf(
        TripId("Trip-1") to listOf(StopId("A"), StopId("B"), StopId("C")),
      ),
    )

    assertThat(result.dot()).isEqualTo(
      """
      |strict digraph G {
      |  A_0 [ label="A_0 [Trip-1]" ];
      |  B_0 [ label="B_0 [Trip-1]" ];
      |  C_0 [ label="C_0 [Trip-1]" ];
      |  A_0 -> B_0;
      |  B_0 -> C_0;
      |}
      """.trimMargin(),
    )
  }

  @Test
  fun `diverging terminals`() {
    val result = buildGraph(
      mapOf(
        TripId("Trip-1") to listOf(StopId("A"), StopId("B"), StopId("C"), StopId("D")),
        TripId("Trip-2") to listOf(StopId("A"), StopId("B"), StopId("E"), StopId("F")),
      ),
    )

    assertThat(result.dot()).isEqualTo(
      """
      |strict digraph G {
      |  A_0 [ label="A_0 [Trip-1, Trip-2]" ];
      |  B_0 [ label="B_0 [Trip-1, Trip-2]" ];
      |  C_0 [ label="C_0 [Trip-1]" ];
      |  D_0 [ label="D_0 [Trip-1]" ];
      |  E_0 [ label="E_0 [Trip-2]" ];
      |  F_0 [ label="F_0 [Trip-2]" ];
      |  A_0 -> B_0;
      |  B_0 -> C_0;
      |  C_0 -> D_0;
      |  B_0 -> E_0;
      |  E_0 -> F_0;
      |}
      """.trimMargin(),
    )
  }

  @Test
  fun `diverging origins`() {
    val result = buildGraph(
      mapOf(
        TripId("Trip-1") to listOf(StopId("A"), StopId("B"), StopId("C"), StopId("D")),
        TripId("Trip-2") to listOf(StopId("E"), StopId("F"), StopId("C"), StopId("D")),
      ),
    )

    assertThat(result.dot()).isEqualTo(
      """
      |strict digraph G {
      |  A_0 [ label="A_0 [Trip-1]" ];
      |  B_0 [ label="B_0 [Trip-1]" ];
      |  C_0 [ label="C_0 [Trip-1, Trip-2]" ];
      |  D_0 [ label="D_0 [Trip-1, Trip-2]" ];
      |  E_0 [ label="E_0 [Trip-2]" ];
      |  F_0 [ label="F_0 [Trip-2]" ];
      |  A_0 -> B_0;
      |  B_0 -> C_0;
      |  C_0 -> D_0;
      |  E_0 -> F_0;
      |  F_0 -> C_0;
      |}
      """.trimMargin(),
    )
  }

  @Test
  fun `mid-route loop`() {
    val result = buildGraph(
      mapOf(
        TripId("Trip-1") to listOf(StopId("A"), StopId("B"), StopId("C"), StopId("B"), StopId("D")),
      ),
    )

    assertThat(result.dot()).isEqualTo(
      """
      |strict digraph G {
      |  A_0 [ label="A_0 [Trip-1]" ];
      |  B_0 [ label="B_0 [Trip-1]" ];
      |  C_0 [ label="C_0 [Trip-1]" ];
      |  B_1 [ label="B_1 [Trip-1]" ];
      |  D_0 [ label="D_0 [Trip-1]" ];
      |  A_0 -> B_0;
      |  B_0 -> C_0;
      |  C_0 -> B_1;
      |  B_1 -> D_0;
      |}
      """.trimMargin(),
    )
  }

  @Test
  fun `converging branches`() {
    val result = buildGraph(
      mapOf(
        TripId("Trip-1") to listOf(StopId("A"), StopId("B"), StopId("C"), StopId("D"), StopId("G")),
        TripId("Trip-2") to listOf(StopId("A"), StopId("B"), StopId("E"), StopId("F"), StopId("G")),
      ),
    )

    assertThat(result.dot()).isEqualTo(
      """
      |strict digraph G {
      |  A_0 [ label="A_0 [Trip-1, Trip-2]" ];
      |  B_0 [ label="B_0 [Trip-1, Trip-2]" ];
      |  C_0 [ label="C_0 [Trip-1]" ];
      |  D_0 [ label="D_0 [Trip-1]" ];
      |  G_0 [ label="G_0 [Trip-1, Trip-2]" ];
      |  E_0 [ label="E_0 [Trip-2]" ];
      |  F_0 [ label="F_0 [Trip-2]" ];
      |  A_0 -> B_0;
      |  B_0 -> C_0;
      |  C_0 -> D_0;
      |  D_0 -> G_0;
      |  B_0 -> E_0;
      |  E_0 -> F_0;
      |  F_0 -> G_0;
      |}
      """.trimMargin(),
    )
  }

  @Test
  fun `disjoint network`() {
    val result = buildGraph(
      mapOf(
        TripId("Trip-1") to listOf(StopId("A"), StopId("B")),
        TripId("Trip-2") to listOf(StopId("C"), StopId("D")),
      ),
    )

    assertThat(result.dot()).isEqualTo(
      """
      |strict digraph G {
      |  A_0 [ label="A_0 [Trip-1]" ];
      |  B_0 [ label="B_0 [Trip-1]" ];
      |  C_0 [ label="C_0 [Trip-2]" ];
      |  D_0 [ label="D_0 [Trip-2]" ];
      |  A_0 -> B_0;
      |  C_0 -> D_0;
      |}
      """.trimMargin(),
    )
  }

  private fun Graph<StopVertex, DefaultEdge>.dot(): String {
    val exporter = DOTExporter<StopVertex, DefaultEdge> { vertex -> "${vertex.id}_${vertex.visit}" }
    exporter.setVertexAttributeProvider { vertex ->
      mapOf("label" to DefaultAttribute.createAttribute("${vertex.id}_${vertex.visit} ${vertex.trips}"))
    }
    val writer = StringWriter()

    exporter.exportGraph(this, writer)
    return writer.toString().trim().replace("\r\n", "\n")
  }
}
