package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.CalendarDateMapper
import ca.derekellis.kgtfs.db.CalendarMapper
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.domain.model.Calendar
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@Inject
@GtfsDsl
public class CalendarDsl(private val database: GtfsDatabase) {
    public fun onDate(time: LocalDate): Set<Calendar> {
        val exceptions =
            database.calendarDateQueries.getByDate(time, CalendarDateMapper).executeAsList()
                .associateBy { it.serviceId }

        val predicate = when (time.dayOfWeek.value) {
            1 -> Calendar::monday
            2 -> Calendar::tuesday
            3 -> Calendar::wednesday
            4 -> Calendar::thursday
            5 -> Calendar::friday
            6 -> Calendar::saturday
            else -> Calendar::sunday
        }

        val calendars =
            database.calendarQueries.getByDate(time, CalendarMapper).executeAsList()

        return calendars
            .filter { predicate(it) || exceptions[it.serviceId]?.exceptionType == 1 }
            .filter { exceptions[it.serviceId]?.exceptionType != 2 }
            .toSet()
    }

    public fun today(): Set<Calendar> = onDate(LocalDate.now())

    public fun getAll(): List<Calendar> = database.calendarQueries.getAll(CalendarMapper).executeAsList()
}
