package ca.derekellis.kgtfs

import assertk.all
import assertk.assertThat
import assertk.assertions.each
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.prop
import ca.derekellis.kgtfs.csv.Calendar
import ca.derekellis.kgtfs.csv.CalendarDate
import ca.derekellis.kgtfs.csv.GtfsTime
import ca.derekellis.kgtfs.csv.Route
import ca.derekellis.kgtfs.csv.Route.Type
import ca.derekellis.kgtfs.csv.RouteId
import ca.derekellis.kgtfs.csv.ServiceId
import ca.derekellis.kgtfs.csv.ShapeId
import ca.derekellis.kgtfs.csv.Stop
import ca.derekellis.kgtfs.csv.StopId
import ca.derekellis.kgtfs.csv.StopTime
import ca.derekellis.kgtfs.csv.Trip
import ca.derekellis.kgtfs.csv.TripId
import okio.FileSystem
import okio.ForwardingFileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.openZip
import org.junit.Test
import java.time.LocalDate

class FileSystemGtfsReaderTest {
  @Test
  fun `happy path`() {
    val reader = FileSystem.SYSTEM
      .openZip("src/test/resources/2021.zip".toPath())
      .openAsGtfs()

    val stopsReader = reader.stops()
    assertThat(stopsReader.hasNext()).isTrue()
    stopsReader.close()
  }

  @Test
  fun `reader reads all rows from csv`() {
    val reader = FileSystem.SYSTEM
      .openZip("src/test/resources/2021.zip".toPath())
      .openAsGtfs()

    val stopsReader = reader.stops()
    assertThat(stopsReader.asSequence().count()).isEqualTo(5780)
    stopsReader.close()
  }

  @Test
  fun `read stops`() {
    val reader = TestFileSystem.openAsGtfs()

    reader.stops().use { stopsReader ->
      assertThat(stopsReader.next()).isEqualTo(
        Stop(
          id = StopId("AAAA"),
          code = "1111",
          name = "Stop A",
          description = "",
          latitude = 45.400649713047486,
          longitude = -75.67314147949219,
          locationType = Stop.LocationType.Platform,
        ),
      )

      assertThat(stopsReader.next()).prop(Stop::id).isEqualTo(StopId("BBBB"))
      assertThat(stopsReader.next()).prop(Stop::id).isEqualTo(StopId("CCCC"))
      assertThat(stopsReader.next()).prop(Stop::id).isEqualTo(StopId("DDDD"))
      assertThat(stopsReader.next()).prop(Stop::id).isEqualTo(StopId("EEEE"))
      assertThat(stopsReader.next()).prop(Stop::id).isEqualTo(StopId("FFFF"))

      assertThat(stopsReader.hasNext()).isFalse()
    }
  }

  @Test
  fun `read calendar`() {
    val reader = TestFileSystem.openAsGtfs()

    reader.calendar().use { calendarReader ->
      assertThat(calendarReader.next()).isEqualTo(
        Calendar(
          ServiceId("WeekdayServiceA"),
          monday = true,
          tuesday = true,
          wednesday = true,
          thursday = true,
          friday = true,
          saturday = false,
          sunday = false,
          startDate = LocalDate.of(2022, 1, 1),
          endDate = LocalDate.of(2022, 4, 1),
        ),
      )

      assertThat(calendarReader.next()).prop(Calendar::serviceId).isEqualTo(ServiceId("WeekdayServiceB"))
      assertThat(calendarReader.next()).prop(Calendar::serviceId).isEqualTo(ServiceId("SaturdayService"))
      assertThat(calendarReader.next()).prop(Calendar::serviceId).isEqualTo(ServiceId("SundayService"))

      assertThat(calendarReader.hasNext()).isFalse()
    }
  }

  @Test
  fun `read calendar dates`() {
    val reader = TestFileSystem.openAsGtfs()

    reader.calendarDates().use { calendarDatesReader ->
      assertThat(calendarDatesReader.next()).isEqualTo(
        CalendarDate(
          serviceId = ServiceId("WeekdayServiceA"),
          date = LocalDate.of(2022, 2, 7),
          exceptionType = 2,
        ),
      )

      assertThat(calendarDatesReader.next()).isEqualTo(
        CalendarDate(
          serviceId = ServiceId("SaturdayService"),
          date = LocalDate.of(2022, 2, 7),
          exceptionType = 1,
        ),
      )

      assertThat(calendarDatesReader.hasNext()).isFalse()
    }
  }

  @Test
  fun `read routes`() {
    val reader = TestFileSystem.openAsGtfs()

    reader.routes().use { routesReader ->
      assertThat(routesReader.next()).isEqualTo(
        Route(
          id = RouteId("1-333"),
          shortName = "1",
          longName = "",
          desc = "",
          type = Route.Type.Rail,
          color = "DA291C",
          textColor = "FFFFFF",
        ),
      )

      assertThat(routesReader.next()).all {
        prop(Route::id).isEqualTo(RouteId("2-333"))
        prop(Route::type).isEqualTo(Type.Bus)
      }

      assertThat(routesReader.next()).prop(Route::id).isEqualTo(RouteId("161-333"))
      assertThat(routesReader.next()).prop(Route::id).isEqualTo(RouteId("263-333"))

      assertThat(routesReader.hasNext()).isFalse()
    }
  }

  @Test
  fun `read shapes`() {
    val reader = TestFileSystem.openAsGtfs()

    reader.shapes().use { shapesReader ->
      val shapes = shapesReader.asSequence().groupBy { it.id }

      assertThat(shapes.keys).hasSize(4)
      assertThat(shapes.values).each { group ->
        group.hasSize(4)
      }

      assertThat(shapesReader.hasNext()).isFalse()
    }
  }

  @Test
  fun `read stop times`() {
    val reader = TestFileSystem.openAsGtfs()

    reader.stopTimes().use { stopTimesReader ->
      val stopTimes = stopTimesReader.asSequence().groupBy { it.tripId }

      assertThat(stopTimes.keys).hasSize(6)
      assertThat(stopTimes.values).each { group ->
        group.hasSize(4)
      }

      assertThat(stopTimesReader.hasNext()).isFalse()
    }

    reader.stopTimes().use { stopTimesReader ->
      assertThat(stopTimesReader.next()).isEqualTo(
        StopTime(
          tripId = TripId("Trip-1-1"),
          arrivalTime = GtfsTime(10, 0, 0),
          departureTime = GtfsTime(10, 0, 0),
          stopId = StopId("AAAA"),
          stopSequence = 1,
          pickupType = 0,
          dropOffType = 0,
        ),
      )
    }
  }

  @Test
  fun `read trips`() {
    val reader = TestFileSystem.openAsGtfs()

    reader.trips().use { tripsReader ->
      assertThat(tripsReader.next()).isEqualTo(
        Trip(
          routeId = RouteId("1-333"),
          serviceId = ServiceId("WeekdayServiceA"),
          id = TripId("Trip-1-1"),
          headsign = "Inbound",
          directionId = 0,
          blockId = "0001",
          shapeId = ShapeId("1"),
        ),
      )

      assertThat(tripsReader.asSequence().count()).isEqualTo(5)
    }
  }

  private object TestFileSystem : ForwardingFileSystem(FileSystem.SYSTEM) {
    override fun onPathParameter(path: Path, functionName: String, parameterName: String): Path {
      return "src/test/resources/gtfs".toPath() / path
    }
  }
}
