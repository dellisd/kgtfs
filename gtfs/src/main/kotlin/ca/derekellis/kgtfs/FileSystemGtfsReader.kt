package ca.derekellis.kgtfs

import ca.derekellis.kgtfs.csv.Agency
import ca.derekellis.kgtfs.csv.AgencyId
import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.ServiceId
import ca.derekellis.kgtfs.csv.Shape
import ca.derekellis.kgtfs.csv.ShapeId
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.csv.TripId
import com.jsoizo.kotlincsv.csvReader
import okio.BufferedSource
import okio.Closeable
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private class FileSystemGtfsReader(
  private val fileSystem: FileSystem,
) : GtfsReader {
  override fun agency(): AgencyReader {
    val source = fileSystem.source("agency.txt".toPath()).buffer()
    return FileAgencyReader(source)
  }

  override fun calendar(): CalendarReader {
    val source = fileSystem.source("calendar.txt".toPath()).buffer()
    return FileCalendarReader(source)
  }

  override fun calendarDates(): CalendarDatesReader {
    val source = fileSystem.source("calendar_dates.txt".toPath()).buffer()
    return FileCalendarDatesReader(source)
  }

  override fun routes(): RoutesReader {
    val source = fileSystem.source("routes.txt".toPath()).buffer()
    return FileRoutesReader(source)
  }

  override fun shapes(): ShapesReader {
    val source = fileSystem.source("shapes.txt".toPath()).buffer()
    return FileShapesReader(source)
  }

  override fun stops(): StopsReader {
    val source = fileSystem.source("stops.txt".toPath()).buffer()
    return FileStopsReader(source)
  }

  override fun stopTimes(): StopTimesReader {
    val source = fileSystem.source("stop_times.txt".toPath()).buffer()
    return FileStopTimesReader(source)
  }

  override fun trips(): TripsReader {
    val source = fileSystem.source("trips.txt".toPath()).buffer()
    return FileTripsReader(source)
  }

  private class FileAgencyReader(source: BufferedSource) : AgencyReader, CsvReader<Agency>(source) {
    override fun next(): Agency {
      val nextLine = csvIterator.next()

      return Agency(
        id = get(nextLine, "agency_id")?.let { AgencyId(it) },
        name = get(nextLine, "agency_name")!!,
        url = get(nextLine, "agency_url")!!,
        timezone = get(nextLine, "agency_timezone")!!,
        lang = get(nextLine, "agency_lang").nullIfEmpty(),
        phone = get(nextLine, "agency_phone").nullIfEmpty(),
        fareUrl = get(nextLine, "agency_fare_url").nullIfEmpty(),
        email = get(nextLine, "agency_email").nullIfEmpty(),
      )
    }
  }

  private class FileCalendarReader(source: BufferedSource) : CalendarReader, CsvReader<Calendar>(source) {
    override fun next(): Calendar {
      val nextLine = csvIterator.next()

      return Calendar(
        serviceId = ServiceId(get(nextLine, "service_id")!!),
        monday = get(nextLine, "monday") == TRUE,
        tuesday = get(nextLine, "tuesday") == TRUE,
        wednesday = get(nextLine, "wednesday") == TRUE,
        thursday = get(nextLine, "thursday") == TRUE,
        friday = get(nextLine, "friday") == TRUE,
        saturday = get(nextLine, "saturday") == TRUE,
        sunday = get(nextLine, "sunday") == TRUE,
        startDate = get(nextLine, "start_date")!!.let { LocalDate.parse(it, DATE_PATTERN) },
        endDate = get(nextLine, "end_date")!!.let { LocalDate.parse(it, DATE_PATTERN) },
      )
    }
  }

  private class FileCalendarDatesReader(source: BufferedSource) : CalendarDatesReader, CsvReader<CalendarDate>(source) {
    override fun next(): CalendarDate {
      val nextLine = csvIterator.next()

      return CalendarDate(
        serviceId = ServiceId(get(nextLine, "service_id")!!),
        date = get(nextLine, "date")!!.let { LocalDate.parse(it, DATE_PATTERN) },
        exceptionType = getIntSafe(nextLine, "exception_type")!!,
      )
    }
  }

  private class FileRoutesReader(source: BufferedSource) : RoutesReader, CsvReader<Route>(source) {
    override fun next(): Route {
      val nextLine = csvIterator.next()

      return Route(
        id = RouteId(get(nextLine, "route_id")!!),
        shortName = get(nextLine, "route_short_name"),
        longName = get(nextLine, "route_long_name"),
        desc = get(nextLine, "route_desc"),
        type = getIntSafe(nextLine, "route_type")!!.let { Route.Type.valueMap[it]!! },
        url = get(nextLine, "route_url").nullIfEmpty(),
        color = get(nextLine, "route_color").nullIfEmpty(),
        textColor = get(nextLine, "route_text_color").nullIfEmpty(),
      )
    }
  }

  private class FileShapesReader(source: BufferedSource) : ShapesReader, CsvReader<Shape>(source) {
    override fun next(): Shape {
      val nextLine = csvIterator.next()

      return Shape(
        id = ShapeId(get(nextLine, "shape_id")!!),
        latitude = get(nextLine, "shape_pt_lat")!!.toDouble(),
        longitude = get(nextLine, "shape_pt_lon")!!.toDouble(),
        sequence = getIntSafe(nextLine, "shape_pt_sequence")!!,
      )
    }
  }

  private class FileStopsReader(source: BufferedSource) : StopsReader, CsvReader<Stop>(source) {
    override fun next(): Stop {
      val nextLine = csvIterator.next()

      return Stop(
        id = StopId(get(nextLine, "stop_id")!!),
        code = get(nextLine, "stop_code"),
        name = get(nextLine, "stop_name"),
        description = get(nextLine, "stop_desc"),
        latitude = get(nextLine, "stop_lat")?.toDoubleOrNull(),
        longitude = get(nextLine, "stop_lon")?.toDoubleOrNull(),
        zoneId = get(nextLine, "zone_id").nullIfEmpty(),
        url = get(nextLine, "stop_url").nullIfEmpty(),
        locationType = getIntSafe(nextLine, "location_type")?.let { Stop.LocationType.entries[it] },
        parentStation = get(nextLine, "parent_station").nullIfEmpty()?.let { StopId(it) },
        timezone = get(nextLine, "stop_timezone").nullIfEmpty(),
        wheelchairBoarding = getIntSafe(nextLine, "wheelchair_boarding"),
        levelId = get(nextLine, "level_id").nullIfEmpty(),
        platformCode = get(nextLine, "platform_code").nullIfEmpty(),
      )
    }
  }

  private class FileStopTimesReader(source: BufferedSource) : StopTimesReader, CsvReader<StopTime>(source) {
    override fun next(): StopTime {
      val nextLine = csvIterator.next()

      return StopTime(
        tripId = TripId(get(nextLine, "trip_id")!!),
        arrivalTime = GtfsTime(get(nextLine, "arrival_time")!!),
        departureTime = GtfsTime(get(nextLine, "departure_time")!!),
        stopId = StopId(get(nextLine, "stop_id")!!),
        stopSequence = getIntSafe(nextLine, "stop_sequence")!!,
        stopHeadsign = get(nextLine, "stop_headsign"),
        pickupType = getIntSafe(nextLine, "pickup_type"),
        dropOffType = getIntSafe(nextLine, "drop_off_type"),
        continuousPickup = getIntSafe(nextLine, "continuous_pickup"),
        continuousDropOff = getIntSafe(nextLine, "continuous_drop_off"),
        shapeDistTraveled = get(nextLine, "shape_dist_traveled")?.toDouble(),
        timepoint = getIntSafe(nextLine, "timepoint"),

      )
    }
  }

  private class FileTripsReader(source: BufferedSource) : TripsReader, CsvReader<Trip>(source) {
    override fun next(): Trip {
      val nextLine = csvIterator.next()

      return Trip(
        routeId = RouteId(get(nextLine, "route_id")!!),
        serviceId = ServiceId(get(nextLine, "service_id")!!),
        id = TripId(get(nextLine, "trip_id")!!),
        headsign = get(nextLine, "trip_headsign"),
        shortName = get(nextLine, "trip_short_name"),
        directionId = getIntSafe(nextLine, "direction_id"),
        blockId = get(nextLine, "block_id").nullIfEmpty(),
        shapeId = get(nextLine, "shape_id").nullIfEmpty()?.let { ShapeId(it) },
        wheelchairAccessible = getIntSafe(nextLine, "wheelchair_accessible"),
        bikesAllowed = get(nextLine, "bikes_allowed").nullIfEmpty()?.let { it == TRUE },
      )
    }
  }

  override fun close() {
  }

  private abstract class CsvReader<T>(protected val source: BufferedSource) : Closeable, Iterator<T> {
    private val lineSequence = sequence {
      while (!source.exhausted()) {
        val line = source.readUtf8Line() ?: break
        yield(line)
      }
      source.close()
    }

    private val csvReader = csvReader()
    protected val csvIterator = csvReader.read(
      lineSequence
        .flatMap { it.asSequence() + sequenceOf('\n') },
    ).iterator()

    protected val headers: Map<String, Int> = csvIterator.next().mapIndexed { index, string -> string to index }.toMap()

    fun get(row: List<String>, header: String): String? {
      val index = headers[header] ?: return null
      return row[index]
    }

    fun getIntSafe(row: List<String>, header: String): Int? {
      val rawValue = get(row, header)
      if (rawValue.isNullOrBlank()) return null

      return try {
        rawValue.toInt()
      } catch (_: NumberFormatException) {
        null
      }
    }

    override fun hasNext(): Boolean {
      return csvIterator.hasNext()
    }

    override fun close() {
      source.close()
    }
  }

  companion object {
    private const val TRUE = "1"
    private val DATE_PATTERN: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    private fun String?.nullIfEmpty(): String? {
      return takeUnless { this.isNullOrBlank() }
    }
  }
}

public fun FileSystem.openAsGtfs(): GtfsReader = FileSystemGtfsReader(this)
