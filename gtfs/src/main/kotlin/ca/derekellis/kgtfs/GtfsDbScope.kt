package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.read.GtfsScopeDsl
import ca.derekellis.kgtfs.db2.Agencies as AgenciesTable
import ca.derekellis.kgtfs.db2.CalendarDates as CalendarDatesTable
import ca.derekellis.kgtfs.db2.Calendars as CalendarsTable
import ca.derekellis.kgtfs.db2.Routes as RoutesTable
import ca.derekellis.kgtfs.db2.Shapes as ShapesTable
import ca.derekellis.kgtfs.db2.StopTimes as StopTimesTable
import ca.derekellis.kgtfs.db2.Stops as StopsTable
import ca.derekellis.kgtfs.db2.Trips as TripsTable

public class GtfsDbScope {
  @GtfsScopeDsl
  public val Agencies: AgenciesTable = AgenciesTable

  @GtfsScopeDsl
  public val CalendarDates: CalendarDatesTable = CalendarDatesTable

  @GtfsScopeDsl
  public val Calendars: CalendarsTable = CalendarsTable

  @GtfsScopeDsl
  public val Routes: RoutesTable = RoutesTable

  @GtfsScopeDsl
  public val Shapes: ShapesTable = ShapesTable

  @GtfsScopeDsl
  public val Stops: StopsTable = StopsTable

  @GtfsScopeDsl
  public val StopTimes: StopTimesTable = StopTimesTable

  @GtfsScopeDsl
  public val Trips: TripsTable = TripsTable
}
