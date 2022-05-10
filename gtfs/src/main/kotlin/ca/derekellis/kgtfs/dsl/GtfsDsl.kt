package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.di.ScriptComponent
import ca.derekellis.kgtfs.di.create
import io.ktor.http.Url
import java.io.File
import java.net.URI

@DslMarker
public annotation class GtfsDsl

@GtfsDsl
public suspend fun gtfs(source: String, dbPath: String = "gtfs.db", block: StaticGtfsScope.() -> Unit) {
    Gtfs(source, dbPath).invoke(block)
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
    public operator fun invoke(block: StaticGtfsScope.() -> Unit) {
        scriptComponent.taskDsl().run(block)
    }

    public fun <R> withResult(block: StaticGtfsScope.() -> R): R {
        return scriptComponent.taskDsl().run(block)
    }
}
