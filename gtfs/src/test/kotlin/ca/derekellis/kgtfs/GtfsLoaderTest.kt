package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.dsl.Gtfs
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class GtfsLoaderTest {
    @get:Rule
    val gtfs: GtfsZipRule = GtfsZipRule()

    @Test
    fun `gtfs is parsed correctly`() {
        val gtfs = runBlocking { Gtfs(gtfs.zip) }

        gtfs {
            assertEquals(stops.getAll().size, 6)
            assertEquals(trips.getAll().size, 2)
        }
    }
}