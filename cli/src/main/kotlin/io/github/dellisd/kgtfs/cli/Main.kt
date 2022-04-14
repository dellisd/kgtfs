package io.github.dellisd.kgtfs.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import io.github.dellisd.kgtfs.cli.script.GtfsScriptHost

class MainCommand : CliktCommand() {
    private val script by argument().file(mustExist = true, canBeDir = false)

    override fun run() {
        GtfsScriptHost().execFile(script.path)
    }
}

fun main(vararg args: String) {
    MainCommand().main(args)
}
