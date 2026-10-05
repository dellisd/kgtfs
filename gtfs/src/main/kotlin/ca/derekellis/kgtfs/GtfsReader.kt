package ca.derekellis.kgtfs

import java.io.Closeable

public interface GtfsReader : Closeable {
  public fun agency(): AgencyReader
  public fun calendar(): CalendarReader
  public fun calendarDates(): CalendarDatesReader
  public fun routes(): RoutesReader
  public fun shapes(): ShapesReader
  public fun stops(): StopsReader
  public fun stopTimes(): StopTimesReader
  public fun trips(): TripsReader
}
