package ca.derekellis.kgtfs.domain

import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.di.ScriptScope
import ca.derekellis.kgtfs.domain.model.Agency
import ca.derekellis.kgtfs.domain.model.Calendar
import ca.derekellis.kgtfs.domain.model.CalendarDate
import ca.derekellis.kgtfs.domain.model.Route
import ca.derekellis.kgtfs.domain.model.Shape
import ca.derekellis.kgtfs.domain.model.Stop
import ca.derekellis.kgtfs.domain.model.StopTime
import ca.derekellis.kgtfs.domain.model.Trip
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.serializer
import me.tatarka.inject.annotations.Inject
import org.slf4j.LoggerFactory
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.div
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.readText

@Inject
@ScriptScope
@OptIn(ExperimentalSerializationApi::class)
public class GtfsLoader(private val database: GtfsDatabase) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val httpClient = HttpClient(CIO)

    public suspend fun loadFromPath(zip: Path) {
        val metadata = database.metadataQueries.get().executeAsOneOrNull()
        val lastModified = zip.getLastModifiedTime().toInstant()

        if (metadata == null || metadata.last_updated < lastModified) {
            logger.info("Loading GTFS from $zip")
            val root = if (zip.isDirectory()) zip else FileSystems.newFileSystem(zip, this::class.java.classLoader).getPath("/")
            withContext(Dispatchers.IO) { readFromPath(root, zip.toString(), lastModified) }
        } else {
            logger.info("GTFS cache is up to date. Skipping.")
        }
    }

    public suspend fun loadFromUrl(url: Url) {
        val temp = withContext(Dispatchers.IO) {
            Files.createTempFile(null, null)
        }

        val metadata = database.metadataQueries.get().executeAsOneOrNull()

        val head = httpClient.head(url)
        val lastModified =
            ZonedDateTime.parse(head.headers["Last-Modified"], DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()

        if (metadata == null || metadata.last_updated < lastModified) {
            logger.info("Downloading GTFS zip from $url")
            var lastPrintPercent = 0
            val response = httpClient.get(url) {
                onDownload { bytesSentTotal, contentLength ->
                    val newPercent = ((bytesSentTotal.toDouble() / contentLength) * 100).toInt()
                    if (newPercent % 10 == 0 && newPercent != lastPrintPercent) {
                        lastPrintPercent = newPercent
                        logger.info("Downloading GTFS zip $lastPrintPercent%")
                    }
                }
            }
            response.bodyAsChannel().copyAndClose(temp.toFile().writeChannel())

            logger.info("Successfully downloaded GTFS zip")
            withContext(Dispatchers.IO) {
                readFromPath(FileSystems.newFileSystem(temp, this::class.java.classLoader).getPath("/"), url.toString(), lastModified)
            }
        } else {
            logger.info("GTFS cache is up to date. Skipping download.")
        }
    }

    private fun readFromPath(root: Path, source: String, lastUpdated: Instant) {
        database.metadataQueries.clear()
        database.metadataQueries.insert(source, lastUpdated)

        read<Agency>(root / "agency.txt") { agencies ->
            database.transaction {
                agencies.forEach {
                    database.agencyQueries.insert(
                        it.id,
                        it.name,
                        it.url,
                        it.timezone,
                        it.lang,
                        it.phone,
                        it.fareUrl,
                        it.email
                    )
                }
            }
        }

        read<Stop>(root / "stops.txt") { stops ->
            database.transaction {
                stops.forEach {
                    database.stopQueries.insert(
                        it.id,
                        it.code,
                        it.name,
                        it.description,
                        it.latitude,
                        it.longitude,
                        it.zoneId,
                        it.url,
                        it.locationType
                    )
                }
            }
        }

        read<Trip>(root / "trips.txt") { stops ->
            database.transaction {
                stops.forEach {
                    database.tripQueries.insert(
                        it.routeId,
                        it.serviceId,
                        it.id,
                        it.headsign,
                        it.directionId,
                        it.blockId,
                        it.shapeId
                    )
                }
            }
        }

        read<StopTime>(root / "stop_times.txt") { times ->
            database.transaction {
                times.forEach {
                    database.stopTimeQueries.insert(
                        it.tripId,
                        it.arrivalTime,
                        it.departureTime,
                        it.stopId,
                        it.stopSequence,
                        it.pickupType,
                        it.dropOffType
                    )
                }
            }
        }

        read<Calendar>(root / "calendar.txt") { calendars ->
            database.transaction {
                calendars.forEach {
                    database.calendarQueries.insert(
                        it.serviceId,
                        it.monday,
                        it.tuesday,
                        it.wednesday,
                        it.thursday,
                        it.friday,
                        it.saturday,
                        it.sunday,
                        it.startDate,
                        it.endDate
                    )
                }
            }
        }

        read<Shape>(root / "shapes.txt") { shapes ->
            database.transaction {
                shapes.forEach {
                    database.shapeQueries.insert(
                        it.id,
                        it.latitude,
                        it.longitude,
                        it.sequence
                    )
                }
            }
        }

        read<Route>(root / "routes.txt") { routes ->
            database.transaction {
                routes.forEach {
                    database.routeQueries.insert(
                        it.id,
                        it.shortName,
                        it.longName,
                        it.desc,
                        it.type,
                        it.url,
                        it.color,
                        it.textColor
                    )
                }
            }
        }

        read<CalendarDate>(root / "calendar_dates.txt") { dates ->
            database.transaction {
                dates.forEach {
                    database.calendarDateQueries.insert(
                        it.serviceId,
                        it.date,
                        it.exceptionType
                    )
                }
            }
        }
    }

    private inline fun <reified T> read(path: Path, block: (List<T>) -> Unit) {
        val name = path.name

        if (path.notExists()) {
            logger.error("Cannot read $name")
            return
        }

        logger.info("Reading $name")

        val text = path.readText().trim('\uFEFF')

        val cr = text.contains("\r")
        val csv = Csv {
            hasHeaderRecord = true
            recordSeparator = if (cr) "\r\n" else "\n"
        }

        val items: List<T> = csv.decodeFromString(ListSerializer(serializer()), text)
        logger.info("Parsed from $name")

        block(items)
    }
}