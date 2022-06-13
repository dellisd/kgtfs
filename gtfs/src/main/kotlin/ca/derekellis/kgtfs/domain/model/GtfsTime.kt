package ca.derekellis.kgtfs.domain.model

import ca.derekellis.kgtfs.domain.serial.GtfsTimeSerializer
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable(with = GtfsTimeSerializer::class)
public data class GtfsTime(val hour: Int, val minute: Int, val second: Int) : Comparable<GtfsTime> {
    public constructor(time: String) : this(
        time.slice(0..1).toInt(),
        time.slice(3..4).toInt(),
        time.slice(6..7).toInt()
    )

    init {
        check(second < 60) { "Seconds greater than or equal to 60: $second" }
        check(minute < 60) { "Minutes greater than or equal to 60: $minute" }
    }

    public fun toLocalDateTime(date: LocalDate): LocalDateTime = when {
        hour >= 24 -> date.atTime(LocalTime.of(hour - 24, minute)) + Duration.ofDays(1)
        else -> date.atTime(LocalTime.of(hour, minute))
    }

    override operator fun compareTo(other: GtfsTime): Int =
        inSeconds - other.inSeconds

    public operator fun plus(duration: Duration): GtfsTime {
        val seconds = second + duration.toSecondsPart()
        val newSeconds = seconds % 60

        val minutes = minute + duration.toMinutesPart() + ((seconds - newSeconds) / 60)
        val newMinutes = minutes % 60

        val hours = hour + duration.toHoursPart() + ((minutes - newMinutes) / 60)

        return GtfsTime(hours, newMinutes, newSeconds)
    }

    public operator fun minus(other: GtfsTime): Duration =
        Duration.ofSeconds((inSeconds - other.inSeconds).toLong())

    private val inSeconds: Int
        get() = hour * 60 * 60 + minute * 60 + second

    override fun toString(): String = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${
        second.toString().padStart(2, '0')
    }"

    public companion object {
        public val MAX: GtfsTime = GtfsTime(48, 59, 59)
        public val MIN: GtfsTime = GtfsTime(0, 0, 0)
    }
}

public fun LocalDateTime.toGtfsTime(): GtfsTime = GtfsTime(hour, minute, second)
public fun LocalTime.toGtfsTime(): GtfsTime = GtfsTime(hour, minute, second)
