package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsFileSystem
import ca.derekellis.kgtfs.openAsGtfs
import com.google.common.truth.Truth.assertThat
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.pathString

@OptIn(ExperimentalKgtfsApi::class)
class GtfsDbTest {
  @Test
  fun `open existing database works`() {
    val dbPath = Files.createTempFile("gtfs-reader", null)
    val reader = GtfsFileSystem.openAsGtfs()

    // Create a database
    GtfsDb.fromReader(reader, path = dbPath.pathString)

    // Open the same database
    val db = GtfsDb.open(path = dbPath.pathString)
    db.query {
      assertThat(Stops.selectAll().count()).isEqualTo(6L)
    }
  }

  @Test
  fun `open new database creates schema`() {
    val dbPath = Files.createTempFile("gtfs-reader", null)

    // Open the same database
    val db = GtfsDb.open(path = dbPath.pathString)
    db.query {
      assertThat(Stops.selectAll().count()).isEqualTo(0L)
    }
  }
}
