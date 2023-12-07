package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.io.GtfsReader
import com.google.common.truth.Truth.assertThat
import org.jetbrains.exposed.sql.selectAll
import org.junit.Rule
import org.junit.Test
import java.nio.file.Files

@OptIn(ExperimentalKgtfsApi::class)
class GtfsDbTest {
  @get:Rule
  val gtfs: GtfsZipRule = GtfsZipRule()

  @Test
  fun `open existing database works`() {
    val dbPath = Files.createTempFile("gtfs-reader", null)
    val reader = GtfsReader.newZipReader(gtfs.zip)

    // Create a database
    GtfsDb.fromReader(reader, path = dbPath)

    // Open the same database
    val db = GtfsDb.open(path = dbPath)
    db.query {
      assertThat(Stops.selectAll().count()).isEqualTo(6L)
    }
  }

  @Test
  fun `open new database creates schema`() {
    val dbPath = Files.createTempFile("gtfs-reader", null)

    // Open the same database
    val db = GtfsDb.open(path = dbPath)
    db.query {
      assertThat(Stops.selectAll().count()).isEqualTo(0L)
    }
  }
}
