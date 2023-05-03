package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.ExperimentalKgtfsApi
import ca.derekellis.kgtfs.GtfsDb
import ca.derekellis.kgtfs.GtfsZipRule
import ca.derekellis.kgtfs.cache.GtfsCache
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.io.GtfsReader
import org.junit.Rule
import java.nio.file.Files
import java.time.Duration
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalKgtfsApi::class)
class TripAlgorithmsTest {
    @get:Rule
    val gtfsRule: GtfsZipRule = GtfsZipRule()

    private lateinit var db: GtfsDb

    @BeforeTest
    fun setup() {
        db = GtfsDb.fromReader(GtfsReader(gtfsRule.zip), Files.createTempFile("gtfs-reader", null))
    }

    @Test
    fun `unique trip sequences are computed correctly`() {
        db.query {
            val sequences = uniqueTripSequences(date = TEST_DATE)

            assertEquals(2, sequences.size)
            sequences.forEach {
                if (it.sequence.first().value == "DDDD") {
                    assertEquals(1, it._trips.size)
                } else {
                    assertEquals(5, it._trips.size)
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
            assertEquals(Duration.ofMinutes(20), frequency)
        }
    }

    @Test
    fun `invalid sequence frequency returns null`() {
        db.query {
            val sequence = uniqueTripSequences(date = TEST_DATE)
                .first { it.sequence.first().value == "DDDD" }

            val frequency = sequence.frequency(GtfsTime("10:00:00"), GtfsTime("11:30:00"))
            assertNull(frequency)
        }
    }

    companion object {
        private val TEST_DATE = LocalDate.of(2022, 2, 1)
    }
}