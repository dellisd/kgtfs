@file:OptIn(ExperimentalContracts::class)

package ca.derekellis.kgtfs.dsl

import ca.derekellis.kgtfs.di.ScriptComponent
import ca.derekellis.kgtfs.di.create
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URI
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@DslMarker
public annotation class GtfsDsl

@GtfsDsl
public fun gtfs(source: String, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> Unit) {
    gtfsWithResult(source, dbPath, scope)
}

@GtfsDsl
public fun <R> gtfsWithResult(source: String, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> R) {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    val asUri = URI(source)
    val scheme = asUri.scheme ?: ""

    val zip = if (scheme.startsWith("http")) {
        GtfsZip.Remote(Url(asUri))
    } else {
        GtfsZip.Local(File(asUri))
    }
    gtfsWithResult(zip, dbPath, scope)
}

@GtfsDsl
public fun gtfs(file: File, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> Unit) {
    gtfsWithResult(file, dbPath, scope)
}

@GtfsDsl
public fun <R> gtfsWithResult(file: File, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> R): R {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    val zip = GtfsZip.Local(file)
    return gtfsWithResult(zip, dbPath, scope)
}

@GtfsDsl
public fun <R> gtfsWithResult(zip: GtfsZip, dbPath: String = "gtfs.db", scope: StaticGtfsScope.() -> R): R {
    contract {
        callsInPlace(scope, InvocationKind.EXACTLY_ONCE)
    }
    val scriptComponent: ScriptComponent = ScriptComponent::class.create(dbPath)

    runBlocking {
        when (zip) {
            is GtfsZip.Local -> scriptComponent.gtfsLoader.loadFrom(zip.file.toPath())
            is GtfsZip.Remote -> scriptComponent.gtfsLoader.loadFrom(zip.url)
        }
    }

    return scriptComponent.taskDsl().run(scope)
}
