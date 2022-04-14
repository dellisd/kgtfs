package io.github.dellisd.kgtfs.cli.script

import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

@KotlinScript(fileExtension = "gtfs.kts", compilationConfiguration = GtfsScriptConfiguration::class)
abstract class GtfsScript

object GtfsScriptConfiguration : ScriptCompilationConfiguration({
    defaultImports("io.github.dellisd.kgtfs.dsl.kgtfs")
})
