package ca.derekellis.kgtfs.io

import ca.derekellis.kgtfs.cache.GtfsCache
import ca.derekellis.kgtfs.csv.AgencyFactory
import ca.derekellis.kgtfs.csv.CalendarDateFactory
import ca.derekellis.kgtfs.csv.CalendarFactory
import ca.derekellis.kgtfs.csv.CsvFactory
import ca.derekellis.kgtfs.csv.RouteFactory
import ca.derekellis.kgtfs.csv.ShapeFactory
import ca.derekellis.kgtfs.csv.StopFactory
import ca.derekellis.kgtfs.csv.StopTimeFactory
import ca.derekellis.kgtfs.csv.TripFactory
import ca.derekellis.kgtfs.isZipFile
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.div
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
  public fun intoCache(location: Path): GtfsCache {
    val root = root()
    val cache = GtfsCache.open(location)

    read(root / "agency.txt", factory = AgencyFactory, block = cache::writeAgencies)
    read(root / "calendar.txt", factory = CalendarFactory, block = cache::writeCalendars)
    read(root / "calendar_dates.txt", factory = CalendarDateFactory, block = cache::writeCalendarDates)
    read(root / "routes.txt", factory = RouteFactory, block = cache::writeRoutes)
    read(root / "stops.txt", factory = StopFactory, block = cache::writeStops)
    read(root / "shapes.txt", factory = ShapeFactory, block = cache::writeShapes)
    read(root / "trips.txt", factory = TripFactory, block = cache::writeTrips)
    read(root / "stop_times.txt", factory = StopTimeFactory, block = cache::writeStopTimes)

    return cache
  }

  private fun root(): Path {
    if (path.isDirectory()) return path
    if (path.isZipFile()) return FileSystems.newFileSystem(path, this::class.java.classLoader).getPath("/")

    throw IllegalStateException("$path does not point to a valid zip file or directory.")
  }

  private fun <T> read(path: Path, factory: CsvFactory<T>, block: (Sequence<T>) -> Unit) {
    csvReader().open(path.inputStream()) {
      readAllWithHeaderAsSequence()
        .map { it.factory() }
        .also(block)
    }
  }
}
