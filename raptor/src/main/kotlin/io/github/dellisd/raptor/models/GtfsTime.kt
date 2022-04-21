package io.github.dellisd.raptor.models

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class GtfsTime(val hour: Int, val minute: Int, val second: Int) : Comparable<GtfsTime> {
    constructor(time: String) : this(time.slice(0..1).toInt(), time.slice(3..4).toInt(), time.slice(6..7).toInt())

    fun toLocalDateTime(date: LocalDate): LocalDateTime = when {
        hour >= 24 -> date.atTime(LocalTime.of(hour - 24, minute)) + Duration.ofDays(1)
        else -> date.atTime(LocalTime.of(hour, minute))
    }

    override operator fun compareTo(other: GtfsTime): Int =
        (hour * 60 * 60 + minute * 60 + second) - (other.hour * 60 * 60 + other.minute * 60 + other.second)

    operator fun plus(duration: Duration): GtfsTime =
        GtfsTime(hour + duration.toHoursPart(), minute + duration.toMinutesPart(), second + duration.toSecondsPart())

    companion object {
        val MAX = GtfsTime(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        val MIN = GtfsTime(Int.MIN_VALUE, Int.MIN_VALUE, Int.MAX_VALUE)
    }
}

fun LocalDateTime.toGtfsTime(): GtfsTime = GtfsTime(hour, minute, second)
