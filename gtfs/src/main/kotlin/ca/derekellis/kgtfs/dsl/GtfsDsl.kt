package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.di.ScriptComponent
import ca.derekellis.kgtfs.di.create
import ca.derekellis.kgtfs.isSqliteFile
import ca.derekellis.kgtfs.isZipFile
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
public fun Gtfs(source: Path, dbPath: String = ""): Gtfs {
    val zip = when {
        source.isDirectory() -> GtfsZip.LocalDirectory(source)
        source.isSqliteFile() -> return Gtfs(GtfsZip.LocalSqliteFile(source), source.toString())
        source.isZipFile() -> GtfsZip.Local(source)
        else -> throw IllegalArgumentException("Unsupported file type")
    }

    return Gtfs(zip, dbPath)
}

@GtfsDsl
public fun Gtfs(source: String, dbPath: String = ""): Gtfs {
    val asUri = URI(source)
    val scheme = asUri.scheme ?: ""

    val zip = if (scheme.startsWith("http")) {
        GtfsZip.Remote(Url(asUri))
    } else {
        val path = Path(asUri.toString())
        when {
            path.isDirectory() -> GtfsZip.LocalDirectory(path)
            path.isSqliteFile() -> return Gtfs(GtfsZip.LocalSqliteFile(path), path.toString())
            path.isZipFile() -> GtfsZip.Local(path)
            else -> throw IllegalArgumentException("Unsupported file type")
        }
    }

    return Gtfs(zip, dbPath)
}

@GtfsDsl
public fun Gtfs(zip: GtfsZip, dbPath: String = ""): Gtfs {
    val actualDbPath = if (zip is GtfsZip.LocalSqliteFile) zip.path.toString() else dbPath
    val scriptComponent = ScriptComponent::class.create(actualDbPath)

    return Gtfs(zip, actualDbPath, scriptComponent)
}

public class Gtfs internal constructor(
    private val zip: GtfsZip,
    private val dbPath: String = "",
    private val scriptComponent: ScriptComponent = ScriptComponent::class.create(dbPath),
) {
    private var initialized: Boolean = false

    private suspend fun ensureInitialized() {
        if (!initialized) {
            when (zip) {
                is GtfsZip.Local -> scriptComponent.gtfsLoader.loadFromPath(zip.path)
                is GtfsZip.Remote -> scriptComponent.gtfsLoader.loadFromUrl(zip.url)
                is GtfsZip.LocalDirectory -> scriptComponent.gtfsLoader.loadFromPath(zip.path)
                is GtfsZip.LocalSqliteFile -> { /* no-op, database is located directly */ }
            }
            initialized = true
        }
    }

    public suspend operator fun <R> invoke(block: StaticGtfsScope.() -> R): R {
        ensureInitialized()
        return scriptComponent.taskDsl().run(block)
    }

    public suspend fun <R> edit(block: MutableStaticGtfsScope.() -> R): R {
        ensureInitialized()
        return scriptComponent.mutableTaskDsl().run(block)
    }

    public suspend fun exportCSV(path: String) {
        exportCSV(Path(path))
    }

    @OptIn(ExperimentalSerializationApi::class)
    public suspend fun exportCSV(path: Path) {
        check(path.isDirectory()) { "Path should be a directory" }
        ensureInitialized()

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

    /**
     * Closes the underlying SQLite database.
     * All querying and editing of this GTFS object after calling this method will fail.
     */
    public fun close() {
        scriptComponent.sqlDriver().close()
    }
}
