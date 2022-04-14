package io.github.dellisd.kgtfs.dsl

import io.github.dellisd.kgtfs.di.ScriptComponent
import io.github.dellisd.kgtfs.di.create
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URI

public class KGtfsBuilder {
    private val scriptComponent: ScriptComponent = ScriptComponent::class.create()

    public var source: GtfsZip? = null
        private set

    public fun gtfs(uri: String) {
        val asUri = URI(uri)
        val scheme = asUri.scheme ?: ""
        source = if (scheme.startsWith("http")) {
            GtfsZip.Remote(Url(asUri))
        } else {
            GtfsZip.Local(File(asUri))
        }
        loadGtfs()
    }

    public fun gtfs(file: File) {
        source = GtfsZip.Local(file)
        loadGtfs()
    }

    public fun task(builder: TaskDsl.() -> Unit) {
        scriptComponent.taskDsl().apply(builder)
    }

    private fun loadGtfs() {
        requireNotNull(source)

        runBlocking {
            when (val source = source) {
                is GtfsZip.Local -> scriptComponent.gtfsLoader.loadFrom(source.file.toPath())
                is GtfsZip.Remote -> scriptComponent.gtfsLoader.loadFrom(source.url)
                null -> throw IllegalStateException()
            }
        }
    }
}

public fun kgtfs(builder: KGtfsBuilder.() -> Unit) {
    KGtfsBuilder().also(builder)
}
