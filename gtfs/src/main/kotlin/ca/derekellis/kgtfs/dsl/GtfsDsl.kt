package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.di.ScriptComponent
import ca.derekellis.kgtfs.di.create
import io.ktor.http.Url
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.encodeToString
import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.writeText

@DslMarker
public annotation class GtfsDsl

@GtfsDsl
public suspend fun <R> gtfs(source: String, dbPath: String = "gtfs.db", block: StaticGtfsScope.() -> R): R {
    return Gtfs(source, dbPath).invoke(block)
}

@GtfsDsl
public suspend fun Gtfs(source: String, dbPath: String = "gtfs.db"): Gtfs {
    val asUri = URI(source)
    val scheme = asUri.scheme ?: ""

    val zip = if (scheme.startsWith("http")) {
        GtfsZip.Remote(Url(asUri))
    } else {
        GtfsZip.Local(File(asUri))
    }

    return Gtfs(zip, dbPath)
}

@GtfsDsl
public suspend fun Gtfs(zip: GtfsZip, dbPath: String = "gtfs.db"): Gtfs {
    val scriptComponent = ScriptComponent::class.create(dbPath)

    when (zip) {
        is GtfsZip.Local -> scriptComponent.gtfsLoader.loadFrom(zip.file.toPath())
        is GtfsZip.Remote -> scriptComponent.gtfsLoader.loadFrom(zip.url)
    }

    return Gtfs(zip, dbPath, scriptComponent)
}

public class Gtfs internal constructor(
    private val zip: GtfsZip,
    private val dbPath: String = "gtfs.db",
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

    public fun exportCSV(path: Path) {
        check(path.isDirectory()) { "Path should be a directory" }

        val csv = Csv {
            hasHeaderRecord = true
        }

        invoke {
            // TODO: Write other files!!!!!!!!
            val stops = csv.encodeToString(stops.getAll())
            path.resolve("stops.txt").writeText(stops)

            val trips = csv.encodeToString(trips.getAll())
            path.resolve("trips.txt").writeText(trips)

            val stopTimes = csv.encodeToString(stopTimes.getAll())
            path.resolve("stop_times.txt").writeText(stopTimes)
        }
    }
}
