package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.db.Agencies as AgenciesTable
import ca.derekellis.kgtfs.db.CalendarDates as CalendarDatesTable
import ca.derekellis.kgtfs.db.Calendars as CalendarsTable
import ca.derekellis.kgtfs.db.Routes as RoutesTable
import ca.derekellis.kgtfs.db.Shapes as ShapesTable
import ca.derekellis.kgtfs.db.StopTimes as StopTimesTable
import ca.derekellis.kgtfs.db.Stops as StopsTable
import ca.derekellis.kgtfs.db.Trips as TripsTable

public class GtfsDbScope {
  @GtfsDsl
  public val Agencies: AgenciesTable = AgenciesTable

  @GtfsDsl
  public val CalendarDates: CalendarDatesTable = CalendarDatesTable

  @GtfsDsl
  public val Calendars: CalendarsTable = CalendarsTable

  @GtfsDsl
  public val Routes: RoutesTable = RoutesTable

  @GtfsDsl
  public val Shapes: ShapesTable = ShapesTable

  @GtfsDsl
  public val Stops: StopsTable = StopsTable

  @GtfsDsl
  public val StopTimes: StopTimesTable = StopTimesTable

  @GtfsDsl
  public val Trips: TripsTable = TripsTable
}
