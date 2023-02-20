package ca.derekellis.kgtfs.cli

import ca.derekellis.kgtfs.cli.script.GtfsScriptHost
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file

class MainCommand : NoOpCliktCommand() {
}

fun main(vararg args: String) {
  MainCommand().subcommands(ImportCommand()).main(args)
}
