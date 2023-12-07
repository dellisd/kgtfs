package ca.derekellis.kgtfs.io

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsDb
import ca.derekellis.kgtfs.GtfsZipRule
import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.AgencyId
import com.google.common.truth.Truth.assertThat
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.Rule
import org.junit.Test
import java.nio.file.FileSystems
import java.nio.file.Files
import java.sql.Connection
import kotlin.io.path.div
import kotlin.io.path.name
import kotlin.io.path.readLines

@OptIn(ExperimentalKgtfsApi::class)
class GtfsWriterTest {
  @get:Rule
  val gtfs: GtfsZipRule = GtfsZipRule()

  @Test
  fun `gtfs zip archive is created correctly`() {
    val tempDir = Files.createTempDirectory("gtfs-test")
    val zip = tempDir / "test.zip"

    val writer = GtfsWriter.newZipWriter(zip)

    val agency = Agency(
      id = AgencyId("the id"),
      name = "The Agency",
      url = "https://example.com",
      timezone = "UTC",
    )
    writer.writeAgencies(sequenceOf(agency))
    writer.close()

    val zipFs = FileSystems.newFileSystem(zip, this::class.java.classLoader)
    val agencyTxt = zipFs.getPath("/agency.txt")

    assertThat(agencyTxt.readLines()).containsExactly(
      "agency_id,agency_name,agency_url,agency_timezone,agency_lang,agency_phone,agency_fare_url,agency_email",
      "the id,The Agency,https://example.com,UTC,,,,",
    )
  }

  @Test
  fun `gtfs zip read into db and written to directory is correct`() {
    val file = Files.createTempFile("kgtfs", "gtfs.db")
    val tempDir = Files.createTempDirectory("gtfs-test")

    // Required by Exposed
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED

    val db = GtfsDb.fromReader(GtfsReader.newZipReader(gtfs.zip), path = file)
    val writer = GtfsWriter.newDirectoryWriter(tempDir)
    db.intoWriter(writer)

    val files = Files.list(tempDir).toList().map { it.name }
    assertThat(files).containsExactly(
      "agency.txt",
      "calendar.txt",
      "calendar_dates.txt",
      "routes.txt",
      "shapes.txt",
      "stops.txt",
      "stop_times.txt",
      "trips.txt",
    )
  }
}
