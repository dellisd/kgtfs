package ca.derekellis.kgtfs.io

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsDb
import ca.derekellis.kgtfs.GtfsZipRule
import org.jetbrains.exposed.sql.selectAll
import org.junit.Rule
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.assertEquals

@OptIn(ExperimentalKgtfsApi::class)
class GtfsReaderTest {
  @get:Rule
  val gtfs: GtfsZipRule = GtfsZipRule()

  @Test
  fun `gtfs is read correctly`() {
    val reader = GtfsReader(gtfs.zip)
    GtfsDb.fromReader(reader, into = Files.createTempFile("gtfs-reader", null)).query {
      assertEquals(Stops.selectAll().map(Stops.Mapper).size, 6)
      assertEquals(Trips.selectAll().map(Trips.Mapper).size, 6)
    }
  }

  @Test
  fun `gtfs is read correctly from directory`() {
    val reader = GtfsReader(Path("src/test/resources/gtfs"))

    GtfsDb.fromReader(reader, into = Files.createTempFile("gtfs-reader", null)).query {
      assertEquals(Stops.selectAll().map(Stops.Mapper).size, 6)
      assertEquals(Trips.selectAll().map(Trips.Mapper).size, 6)
    }
  }
}
