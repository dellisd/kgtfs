package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.dsl.Gtfs
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class GtfsLoaderTest {
    @get:Rule
    val gtfs: GtfsZipRule = GtfsZipRule()

    @Test
    fun `gtfs is parsed correctly`() = runTest {
        val gtfs = Gtfs(gtfs.zip)

        gtfs {
            assertEquals(stops.getAll().size, 6)
            assertEquals(trips.getAll().size, 6)
        }
    }

    @Test
    fun `gtfs is loaded correctly from directory`() = runTest {
        val gtfs = Gtfs("src/test/resources/gtfs")

        gtfs {
            assertEquals(stops.getAll().size, 6)
            assertEquals(trips.getAll().size, 6)
        }
    }

    @Test
    fun `gtfs is loaded correctly from sqlite file`() = runTest {
        val gtfs = Gtfs("src/test/resources/test.db")
        gtfs {
            assertEquals(6, stops.getAll().size)
            assertEquals(6, trips.getAll().size)
        }
    }
}
