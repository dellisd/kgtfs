package ca.derekellis.kgtfs.cli

import ca.derekellis.kgtfs.cli.script.GtfsScriptHost
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file

class MainCommand : CliktCommand() {
    private val script by argument().file(mustExist = true, canBeDir = false)

    override fun run() {
        GtfsScriptHost().execFile(script.path)
    }
}

fun main(vararg args: String) {
    MainCommand().main(args)
}
