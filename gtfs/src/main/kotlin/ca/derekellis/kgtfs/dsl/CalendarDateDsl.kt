package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.db.CalendarDateMapper
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.csv.CalendarDate
import me.tatarka.inject.annotations.Inject

@Inject
@GtfsDsl
public class CalendarDateDsl(private val database: GtfsDatabase) {
    public fun getAll(): List<CalendarDate> = database.calendarDateQueries.getAll(CalendarDateMapper).executeAsList()
}
