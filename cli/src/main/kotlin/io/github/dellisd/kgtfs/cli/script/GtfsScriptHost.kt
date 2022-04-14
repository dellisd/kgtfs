package io.github.dellisd.kgtfs.cli.script

import org.slf4j.LoggerFactory
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class GtfsScriptHost {
    private val logger = LoggerFactory.getLogger(javaClass)

    private fun evalFile(scriptFile: File): ResultWithDiagnostics<EvaluationResult> {
        val compilationConfiguration = createJvmCompilationConfigurationFromTemplate<GtfsScript> {
            jvm {
                dependenciesFromCurrentContext(wholeClasspath = true)
            }
            ide {
                acceptedLocations(ScriptAcceptedLocation.Everywhere)
            }
        }

        return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null)
    }

    fun execFile(path: String) {
        val scriptFile = File(path)
        val res = evalFile(scriptFile)

        res.reports.forEach {
            when (it.severity) {
                ScriptDiagnostic.Severity.DEBUG -> logger.debug(it.message, it.exception)
                ScriptDiagnostic.Severity.INFO -> logger.info(it.message, it.exception)
                ScriptDiagnostic.Severity.WARNING -> logger.warn(it.message, it.exception)
                ScriptDiagnostic.Severity.ERROR -> logger.error(it.message, it.exception)
                ScriptDiagnostic.Severity.FATAL -> logger.error("FATAL: ${it.message}", it.exception)
            }
        }
    }
}
