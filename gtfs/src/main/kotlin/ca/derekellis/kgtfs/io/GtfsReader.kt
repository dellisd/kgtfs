package ca.derekellis.kgtfs.io

import ca.derekellis.kgtfs.cache.GtfsCache
import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.AgencyFactory
import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.CalendarDateFactory
import ca.derekellis.kgtfs.csv.CalendarFactory
import ca.derekellis.kgtfs.csv.CsvFactory
import ca.derekellis.kgtfs.csv.Gtfs
import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.RouteFactory
import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.ShapeFactory
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopFactory
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.StopTimeFactory
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.csv.TripFactory
import ca.derekellis.kgtfs.isZipFile
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory

/**
 * Reader for a [path] that writes all of its GTFS data into a [GtfsCache] database.
 */
public class GtfsReader(
  public val path: Path,
) {
  /**
   * Reads the GTFS data into a [GtfsCache] which will be saved onto disk at the given [location].
   */
  public fun intoCache(location: Path): GtfsCache = GtfsCache.fromReader(location, this)

  private fun root(): Path {
    if (path.isDirectory()) return path
    if (path.isZipFile()) return FileSystems.newFileSystem(path, this::class.java.classLoader).getPath("/")

    throw IllegalStateException("$path does not point to a valid zip file or directory.")
  }

  private fun <T : Gtfs> read(path: String, factory: CsvFactory<T>, block: (Sequence<T>) -> Unit) {
    val file = root() / path
    if (!file.exists()) return

    csvReader().open(file.inputStream()) {
      readAllWithHeaderAsSequence()
        .map { it.factory() }
        .also(block)
    }
  }

  public fun readAgencies(block: (Sequence<Agency>) -> Unit) {
    read("agency.txt", AgencyFactory, block)
  }

  public fun readCalendars(block: (Sequence<Calendar>) -> Unit) {
    read("calendar.txt", CalendarFactory, block)
  }

  public fun readCalendarDates(block: (Sequence<CalendarDate>) -> Unit) {
    read("calendar_dates.txt", CalendarDateFactory, block)
  }

  public fun readRoutes(block: (Sequence<Route>) -> Unit) {
    read("routes.txt", RouteFactory, block)
  }

  public fun readStops(block: (Sequence<Stop>) -> Unit) {
    read("stops.txt", StopFactory, block)
  }

  public fun readStopTimes(block: (Sequence<StopTime>) -> Unit) {
    read("stop_times.txt", StopTimeFactory, block)
  }

  public fun readTrips(block: (Sequence<Trip>) -> Unit) {
    read("trips.txt", TripFactory, block)
  }

  public fun readShapes(block: (Sequence<Shape>) -> Unit) {
    read("shapes.txt", ShapeFactory, block)
  }

  @Suppress("UNCHECKED_CAST")
  public inline fun <reified T : Gtfs> read(noinline block: (Sequence<T>) -> Unit) {
    when (T::class) {
      Agency::class -> readAgencies(block as (Sequence<Agency>) -> Unit)
      Calendar::class -> readCalendars(block as (Sequence<Calendar>) -> Unit)
      CalendarDate::class -> readCalendarDates(block as (Sequence<CalendarDate>) -> Unit)
      Route::class -> readStops(block as (Sequence<Stop>) -> Unit)
      Shape::class -> readShapes(block as (Sequence<Shape>) -> Unit)
      Stop::class -> readStops(block as (Sequence<Stop>) -> Unit)
      StopTime::class -> readStopTimes(block as (Sequence<StopTime>) -> Unit)
      Trip::class -> readTrips(block as (Sequence<Trip>) -> Unit)
    }
  }
}
