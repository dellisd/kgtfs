package ca.derekellis.kgtfs.io

import ca.derekellis.kgtfs.GtfsZipRule
import org.junit.Rule
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.assertEquals

class GtfsReaderTest {
  @get:Rule
  val gtfs: GtfsZipRule = GtfsZipRule()

  @Test
  fun `gtfs is read correctly`() {
    val cache = GtfsReader(gtfs.zip).intoCache(Files.createTempFile("gtfs-reader", null))

    cache.read {
      assertEquals(stops.getAll().size, 6)
      assertEquals(trips.getAll().size, 6)
    }
  }

  @Test
  fun `gtfs is read correctly from directory`() {
    val cache = GtfsReader(Path("src/test/resources/gtfs")).intoCache(Files.createTempFile("gtfs-reader", null))

    cache.read {
      assertEquals(stops.getAll().size, 6)
      assertEquals(trips.getAll().size, 6)
    }
  }
}
