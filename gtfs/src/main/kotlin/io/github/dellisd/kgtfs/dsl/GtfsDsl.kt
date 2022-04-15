package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.di.ScriptComponent
import io.github.dellisd.kgtfs.di.create
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URI

@DslMarker
public annotation class GtfsDsl

public fun gtfs(source: String, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> Unit) {
    val asUri = URI(source)
    val scheme = asUri.scheme ?: ""

    val zip = if (scheme.startsWith("http")) {
        GtfsZip.Remote(Url(asUri))
    } else {
        GtfsZip.Local(File(asUri))
    }
    gtfs(zip, dbPath, scope)
}

public fun gtfs(file: File, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> Unit) {
    val zip = GtfsZip.Local(file)
    gtfs(zip, dbPath, scope)
}

public fun gtfs(zip: GtfsZip, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> Unit) {
    val scriptComponent: ScriptComponent = ScriptComponent::class.create(dbPath)

    runBlocking {
        when (zip) {
            is GtfsZip.Local -> scriptComponent.gtfsLoader.loadFrom(zip.file.toPath())
            is GtfsZip.Remote -> scriptComponent.gtfsLoader.loadFrom(zip.url)
        }
    }

    scriptComponent.taskDsl().apply(scope)
}
