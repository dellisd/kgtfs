package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.domain.model.GtfsTime
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GtfsTimeTest {
    @Test
    fun `time comparison is correct`() {
        val a = GtfsTime(10, 0, 0)
        val b = GtfsTime(11, 0, 0)
        val c = GtfsTime("24:59:00")
        val d = GtfsTime(10, 0, 0)

        assertTrue { a < b }
        assertFalse { b < a }

        assertTrue { c > a }
        assertTrue { c > b }

        assertTrue { a < GtfsTime.MAX }
        assertTrue { b < GtfsTime.MAX }
        assertTrue { c < GtfsTime.MAX }

        assertFalse { a < d }
    }

    @Test
    fun `time addition is correct`() {
        val t = GtfsTime("18:30:45")

        assertEquals(GtfsTime("19:00:45"), t + Duration.ofMinutes(30))
        assertEquals(GtfsTime("18:31:15"), t + Duration.ofSeconds(30))
        assertEquals(GtfsTime("19:30:45"), t + Duration.ofHours(1))
        assertEquals(GtfsTime("19:01:15"), t + (Duration.ofMinutes(30) + Duration.ofSeconds(30)))
    }

    @Test
    fun `time subtraction is correct`() {
        val t = GtfsTime("13:00:00")
        val a = GtfsTime("12:00:00")
        val b = GtfsTime("12:30:00")
        val c = GtfsTime("12:59:30")

        assertEquals(Duration.ofHours(1), t - a)
        assertEquals(Duration.ofMinutes(30), t - b)
        assertEquals(Duration.ofSeconds(30), t - c)
    }
}
