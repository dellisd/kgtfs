package ca.derekellis.kgtfs.raptor.models

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

public data class GtfsTime(val hour: Int, val minute: Int, val second: Int) : Comparable<GtfsTime> {
    public constructor(time: String) : this(
        time.slice(0..1).toInt(),
        time.slice(3..4).toInt(),
        time.slice(6..7).toInt()
    )

    public fun toLocalDateTime(date: LocalDate): LocalDateTime = when {
        hour >= 24 -> date.atTime(LocalTime.of(hour - 24, minute)) + Duration.ofDays(1)
        else -> date.atTime(LocalTime.of(hour, minute))
    }

    override operator fun compareTo(other: GtfsTime): Int =
        (hour * 60 * 60 + minute * 60 + second) - (other.hour * 60 * 60 + other.minute * 60 + other.second)

    public operator fun plus(duration: Duration): GtfsTime =
        GtfsTime(hour + duration.toHoursPart(), minute + duration.toMinutesPart(), second + duration.toSecondsPart())

    public companion object {
        public val MAX: GtfsTime = GtfsTime(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        public val MIN: GtfsTime = GtfsTime(Int.MIN_VALUE, Int.MIN_VALUE, Int.MAX_VALUE)
    }
}

public fun LocalDateTime.toGtfsTime(): GtfsTime = GtfsTime(hour, minute, second)
