package ca.derekellis.kgtfs.db

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsFileSystem
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.openAsGtfs
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import java.time.Duration
import java.time.LocalDate
import kotlin.io.path.pathString

@OptIn(ExperimentalKgtfsApi::class)
class TripAlgorithmsTest {
  private lateinit var db: GtfsDb

  @Before
  fun setup() {
    db = GtfsDb.fromReader(GtfsFileSystem.openAsGtfs(), Files.createTempFile("gtfs-reader", null).pathString)
  }

  @Test
  fun `unique trip sequences are computed correctly`() {
    db.query {
      val sequences = uniqueTripSequences(date = TEST_DATE)

      assertThat(sequences.size).isEqualTo(2)
      sequences.forEach {
        if (it.sequence.first().value == "DDDD") {
          assertThat(it._trips.size).isEqualTo(1)
        } else {
          assertThat(it._trips.size).isEqualTo(5)
        }
      }
    }
  }

  @Test
  fun `sequence frequency computed correctly`() {
    db.query {
      val sequence = uniqueTripSequences(date = TEST_DATE)
        .first { it.sequence.first().value == "AAAA" }

      val frequency = sequence.frequency(GtfsTime("10:00:00"), GtfsTime("11:30:00"))
      assertThat(frequency).isEqualTo(Duration.ofMinutes(20))
    }
  }

  @Test
  fun `invalid sequence frequency returns null`() {
    db.query {
      val sequence = uniqueTripSequences(date = TEST_DATE)
        .first { it.sequence.first().value == "DDDD" }

      val frequency = sequence.frequency(GtfsTime("10:00:00"), GtfsTime("11:30:00"))
      assertThat(frequency).isNull()
    }
  }

  companion object {
    private val TEST_DATE = LocalDate.of(2022, 2, 1)
  }
}
