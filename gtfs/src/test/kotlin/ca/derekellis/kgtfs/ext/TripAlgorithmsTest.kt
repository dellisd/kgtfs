package ca.derekellis.kgtfs.ext

import ca.derekellis.kgtfs.GtfsZipRule
import ca.derekellis.kgtfs.domain.model.GtfsTime
import ca.derekellis.kgtfs.dsl.Gtfs
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import java.time.Duration
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TripAlgorithmsTest {
    @get:Rule
    val gtfsRule: GtfsZipRule = GtfsZipRule()

    private lateinit var gtfs: Gtfs

    @BeforeTest
    fun setup() = runBlocking {
        gtfs = Gtfs(gtfsRule.zip)
    }

    @Test
    fun `unique trip sequences are computed correctly`() = runTest {
        gtfs {
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
    fun `sequence frequency computed correctly`() = runTest {
        gtfs {
            val sequence = uniqueTripSequences(date = TEST_DATE)
                    .first { it.sequence.first().value == "AAAA" }

            val frequency = sequence.frequency(GtfsTime("10:00:00"), GtfsTime("11:30:00"))
            assertEquals(Duration.ofMinutes(20), frequency)
        }
    }

    @Test
    fun `invalid sequence frequency returns null`() = runTest {
        gtfs {
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