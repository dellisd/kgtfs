package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.di.ScriptComponent
import ca.derekellis.kgtfs.di.create
import io.ktor.http.Url
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.encodeToString
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.writeText

@DslMarker
public annotation class GtfsDsl

@GtfsDsl
public suspend fun <R> gtfs(source: String, dbPath: String = "", block: StaticGtfsScope.() -> R): R {
    return Gtfs(source, dbPath).invoke(block)
}

@GtfsDsl
public suspend fun Gtfs(source: Path, dbPath: String = ""): Gtfs {
    val zip = GtfsZip.Local(source)
    return Gtfs(zip, dbPath)
}

@GtfsDsl
public suspend fun Gtfs(source: String, dbPath: String = ""): Gtfs {
    val asUri = URI(source)
    val scheme = asUri.scheme ?: ""

    val zip = if (scheme.startsWith("http")) {
        GtfsZip.Remote(Url(asUri))
    } else {
        GtfsZip.Local(Path(asUri.toString()))
    }

    return Gtfs(zip, dbPath)
}

@GtfsDsl
public suspend fun Gtfs(zip: GtfsZip, dbPath: String = ""): Gtfs {
    val scriptComponent = ScriptComponent::class.create(dbPath)

    when (zip) {
        is GtfsZip.Local -> scriptComponent.gtfsLoader.loadFrom(zip.path)
        is GtfsZip.Remote -> scriptComponent.gtfsLoader.loadFrom(zip.url)
    }

    return Gtfs(zip, dbPath, scriptComponent)
}

public class Gtfs internal constructor(
    private val zip: GtfsZip,
    private val dbPath: String = "",
    private val scriptComponent: ScriptComponent = ScriptComponent::class.create(dbPath)
) {
    public operator fun <R> invoke(block: StaticGtfsScope.() -> R): R {
        return scriptComponent.taskDsl().run(block)
    }

    public fun <R> edit(block: MutableStaticGtfsScope.() -> R): R {
        return scriptComponent.mutableTaskDsl().run(block)
    }

    public fun exportCSV(path: String) {
        exportCSV(Path(path))
    }

    @OptIn(ExperimentalSerializationApi::class)
    public fun exportCSV(path: Path) {
        check(path.isDirectory()) { "Path should be a directory" }

        val csv = Csv {
            hasHeaderRecord = true
            recordSeparator = System.lineSeparator()
        }

        invoke {
            val agencies = csv.encodeToString(agencies.getAll())
            path.resolve("agency.txt").writeText(agencies)

            val calendars = csv.encodeToString(calendar.getAll())
            path.resolve("calendar.txt").writeText(calendars)

            val calendarDates = csv.encodeToString(dates.getAll())
            path.resolve("calendar_dates.txt").writeText(calendarDates)

            val routes = csv.encodeToString(routes.getAll())
            path.resolve("routes.txt").writeText(routes)

            val stops = csv.encodeToString(stops.getAll())
            path.resolve("stops.txt").writeText(stops)

            val trips = csv.encodeToString(trips.getAll())
            path.resolve("trips.txt").writeText(trips)

            val stopTimes = csv.encodeToString(stopTimes.getAll())
            path.resolve("stop_times.txt").writeText(stopTimes)
        }
    }
}
