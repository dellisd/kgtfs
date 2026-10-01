package ca.derekellis.kgtfs.db.io

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsZipRule
import ca.derekellis.kgtfs.db.GtfsDb
import ca.derekellis.kgtfs.io.GtfsReader
import com.google.common.truth.Truth.assertThat
import org.jetbrains.exposed.sql.selectAll
import org.junit.Rule
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.Path

@OptIn(ExperimentalKgtfsApi::class)
class GtfsReaderTest {
  @get:Rule
  val gtfs: GtfsZipRule = GtfsZipRule()

  @Test
  fun `gtfs is read correctly`() {
    val reader = GtfsReader.newZipReader(gtfs.zip)
    GtfsDb.fromReader(reader, path = Files.createTempFile("gtfs-reader", null)).query {
      assertThat(Stops.selectAll().map(Stops.Mapper).size).isEqualTo(6)
      assertThat(Trips.selectAll().map(Trips.Mapper).size).isEqualTo(6)
    }
  }

  @Test
  fun `gtfs is read correctly from directory`() {
    val reader = GtfsReader.newDirectoryReader(Path("src/test/resources/gtfs"))

    GtfsDb.fromReader(reader, path = Files.createTempFile("gtfs-reader", null)).query {
      assertThat(Stops.selectAll().map(Stops.Mapper).size).isEqualTo(6)
      assertThat(Trips.selectAll().map(Trips.Mapper).size).isEqualTo(6)
    }
  }

  @Test
  fun `new gtfs zip is read correctly`() {
    val reader = GtfsReader.newZipReader(Path("src/test/resources/gtfs.zip"))
    GtfsDb.fromReader(reader, path = Files.createTempFile("gtfs-reader", null)).query {
      println(Stops.selectAll().map(Stops.Mapper).size)
    }
  }
}
