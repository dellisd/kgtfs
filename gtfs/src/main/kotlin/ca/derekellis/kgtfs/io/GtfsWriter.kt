package ca.derekellis.kgtfs.io

import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.Gtfs
import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.isZipFile
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.Closeable
import java.nio.file.FileSystems
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.zip.ZipOutputStream
import kotlin.io.path.createDirectory
import kotlin.io.path.div
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

/**
 * Writer for a GTFS dataset to a CSV dataset.
 *
 * @property path Path to a destination for GTFS data, either a directory or a zip archive.
 *
 * @see newZipWriter
 */
public abstract class GtfsWriter(public val path: Path) : Closeable {
  private fun Boolean.csvValue(): String = if (this) "1" else "0"
  private fun LocalDate.csvValue(): String = format(datePattern)

  protected abstract fun root(): Path

  private fun <T : Gtfs> writeInternal(
    path: String,
    headerRow: List<String>,
    rows: Sequence<T>,
    rowFactory: T.() -> List<String?>,
  ) {
    val file = root() / path

    csvWriter().open(file.outputStream()) {
      writeRow(headerRow)
      writeRows(rows.map(rowFactory))
    }
  }

  public fun writeAgencies(rows: Sequence<Agency>) {
    writeInternal(
      path = "agency.txt",
      headerRow = listOf(
        "agency_id",
        "agency_name",
        "agency_url",
        "agency_timezone",
        "agency_lang",
        "agency_phone",
        "agency_fare_url",
        "agency_email",
      ),
      rows = rows,
    ) {
      listOf(id?.value, name, url, timezone, lang, phone, fareUrl, email)
    }
  }

  public fun writeCalendars(rows: Sequence<Calendar>) {
    writeInternal(
      path = "calendar.txt",
      headerRow = listOf(
        "service_id",
        "monday",
        "tuesday",
        "wednesday",
        "thursday",
        "friday",
        "saturday",
        "sunday",
        "start_date",
        "end_date",
      ),
      rows = rows,
    ) {
      listOf(
        serviceId.value,
        monday.csvValue(),
        tuesday.csvValue(),
        wednesday.csvValue(),
        thursday.csvValue(),
        friday.csvValue(),
        saturday.csvValue(),
        sunday.csvValue(),
        startDate.csvValue(),
        sunday.csvValue(),
      )
    }
  }

  public fun writeCalendarDates(rows: Sequence<CalendarDate>) {
    writeInternal(
      path = "calendar_dates.txt",
      headerRow = listOf("service_id", "date", "exception_type"),
      rows = rows,
    ) {
      listOf(serviceId.value, date.csvValue(), exceptionType.toString())
    }
  }

  public fun writeRoutes(rows: Sequence<Route>) {
    writeInternal(
      path = "routes.txt",
      headerRow = listOf(
        "route_id",
        "route_short_name",
        "route_long_name",
        "route_desc",
        "route_type",
        "route_url",
        "route_color",
        "route_text_color",
      ),
      rows = rows,
    ) {
      listOf(id.value, shortName, longName, desc, type.value.toString(), url, color, textColor)
    }
  }

  public fun writeShapes(rows: Sequence<Shape>) {
    writeInternal(
      path = "shapes.txt",
      headerRow = listOf(
        "shape_id",
        "shape_pt_lat",
        "shape_pt_lon",
        "shape_pt_sequence",
      ),
      rows = rows,
    ) {
      listOf(id.value, latitude.toString(), longitude.toString(), sequence.toString())
    }
  }

  public fun writeStops(rows: Sequence<Stop>) {
    writeInternal(
      path = "stops.txt",
      headerRow = listOf(
        "stop_id",
        "stop_code",
        "stop_desc",
        "stop_lat",
        "stop_lon",
        "zone_id",
        "stop_url",
        "location_type",
        "parent_station",
        "stop_timezone",
        "wheelchair_boarding",
        "level_id",
        "platform_code",
      ),
      rows = rows,
    ) {
      listOf(
        id.value,
        code,
        name,
        description,
        latitude?.toString(),
        longitude?.toString(),
        zoneId,
        url,
        locationType?.ordinal?.toString(),
        parentStation?.value,
        timezone,
        wheelchairBoarding?.toString(),
        levelId,
        platformCode,
      )
    }
  }

  public fun writeStopTimes(rows: Sequence<StopTime>) {
    writeInternal(
      path = "stop_times.txt",
      headerRow = listOf(
        "trip_id",
        "arrival_time",
        "departure_time",
        "stop_id",
        "stop_sequence",
        "stop_headsign",
        "pickup_type",
        "drop_off_type",
        "continuous_pickup",
        "continuous_drop_off",
        "shape_dist_travelled",
        "timepoint",
      ),
      rows = rows,
    ) {
      listOf(
        tripId.value,
        arrivalTime.toString(),
        arrivalTime.toString(),
        stopId.value,
        stopSequence.toString(),
        stopHeadsign,
        pickupType?.toString(),
        dropOffType?.toString(),
        continuousPickup?.toString(),
        continuousDropOff?.toString(),
        shapeDistTraveled?.toString(),
        timepoint?.toString(),
      )
    }
  }

  public fun writeTrips(rows: Sequence<Trip>) {
    writeInternal(
      path = "trips.txt",
      headerRow = listOf(
        "route_id",
        "service_id",
        "trip_id",
        "trip_headsign",
        "trip_short_name",
        "direction_id",
        "block_id",
        "shape_id",
        "wheelchair_accessible",
        "bikes_allowed",
      ),
      rows = rows,
    ) {
      listOf(
        routeId.value,
        serviceId.value,
        id.value,
        headsign,
        shortName,
        directionId?.toString(),
        blockId,
        shapeId?.value,
        wheelchairAccessible?.toString(),
        bikesAllowed?.csvValue(),
      )
    }
  }

  @Suppress("UNCHECKED_CAST")
  public inline fun <reified T : Gtfs> write(rows: Sequence<T>) {
    when (T::class) {
      Agency::class -> writeAgencies(rows as Sequence<Agency>)
      Calendar::class -> writeCalendars(rows as (Sequence<Calendar>))
      CalendarDate::class -> writeCalendarDates(rows as (Sequence<CalendarDate>))
      Route::class -> writeRoutes(rows as (Sequence<Route>))
      Shape::class -> writeShapes(rows as (Sequence<Shape>))
      Stop::class -> writeStops(rows as (Sequence<Stop>))
      StopTime::class -> writeStopTimes(rows as (Sequence<StopTime>))
      Trip::class -> writeTrips(rows as (Sequence<Trip>))
    }
  }

  public companion object {
    private val datePattern = DateTimeFormatter.ofPattern("yyyyMMdd")

    public fun newZipWriter(path: Path): GtfsWriter {
      if (path.notExists()) {
        ZipOutputStream(path.outputStream()).close()
      }
      check(path.isZipFile()) { "$path is not a zip file!" }

      return GtfsZipWriter(path)
    }

    public fun newDirectoryWriter(path: Path): GtfsWriter {
      if (path.notExists()) {
        path.createDirectory()
      }
      check(path.isDirectory()) { "$path is not a directory!" }

      return GtfsDirectoryWriter(path)
    }
  }
}

private class GtfsZipWriter(path: Path) : GtfsWriter(path) {
  private val zipFs by lazy { FileSystems.newFileSystem(path, this::class.java.classLoader) }
  override fun root(): Path {
    return zipFs.getPath("/")
  }

  override fun close() {
    zipFs.close()
  }
}

private class GtfsDirectoryWriter(path: Path) : GtfsWriter(path) {
  override fun root(): Path {
    return path
  }

  override fun close() {
    /* No-op */
  }
}
