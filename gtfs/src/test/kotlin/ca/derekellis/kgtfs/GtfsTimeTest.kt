package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.GtfsTime
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Duration

class GtfsTimeTest {
  @Test
  fun `time comparison is correct`() {
    val a = GtfsTime(10, 0, 0)
    val b = GtfsTime(11, 0, 0)
    val c = GtfsTime("24:59:00")
    val d = GtfsTime(10, 0, 0)

    assertThat(a).isLessThan(b)

    assertThat(c).isGreaterThan(a)
    assertThat(c).isGreaterThan(b)

    assertThat(a).isLessThan(GtfsTime.MAX)
    assertThat(b).isLessThan(GtfsTime.MAX)
    assertThat(c).isLessThan(GtfsTime.MAX)

    assertThat(a).isEquivalentAccordingToCompareTo(d)
  }

  @Test
  fun `time addition is correct`() {
    val t = GtfsTime("18:30:45")

    assertThat(t + Duration.ofMinutes(30)).isEqualTo(GtfsTime("19:00:45"))
    assertThat(t + Duration.ofSeconds(30)).isEqualTo(GtfsTime("18:31:15"))
    assertThat(t + Duration.ofHours(1)).isEqualTo(GtfsTime("19:30:45"))
    assertThat(t + (Duration.ofMinutes(30) + Duration.ofSeconds(30))).isEqualTo(GtfsTime("19:01:15"))
  }

  @Test
  fun `time subtraction is correct`() {
    val t = GtfsTime("13:00:00")
    val a = GtfsTime("12:00:00")
    val b = GtfsTime("12:30:00")
    val c = GtfsTime("12:59:30")

    assertThat(t - a).isEqualTo(Duration.ofHours(1))
    assertThat(t - b).isEqualTo(Duration.ofMinutes(30))
    assertThat(t - c).isEqualTo(Duration.ofSeconds(30))
  }
}
