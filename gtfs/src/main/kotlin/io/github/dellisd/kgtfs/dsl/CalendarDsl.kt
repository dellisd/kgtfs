package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.db.Calendar
import io.github.dellisd.kgtfs.db.GtfsDatabase
import me.tatarka.inject.annotations.Inject
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Inject
public class CalendarDsl(private val database: GtfsDatabase) {
    public fun onDate(time: OffsetDateTime): Set<Calendar> {
        val dateString = time.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val exceptions =
            database.calendarDateQueries.getByDate(dateString).executeAsList().associateBy { it.service_id }

        val predicate = when (time.dayOfWeek.value) {
            1 -> Calendar::monday
            2 -> Calendar::tuesday
            3 -> Calendar::wednesday
            4 -> Calendar::thursday
            5 -> Calendar::friday
            6 -> Calendar::saturday
            else -> Calendar::sunday
        }

        val calendars = database.calendarQueries.getByDate(dateString).executeAsList()

        return calendars
            .filter { predicate(it) || exceptions[it.service_id]?.exception_type == 1 }
            .filter { exceptions[it.service_id]?.exception_type != 2 }
            .toSet()
    }

    public fun today(): Set<Calendar> = onDate(OffsetDateTime.now())
}
