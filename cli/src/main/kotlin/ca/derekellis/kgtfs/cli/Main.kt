package ca.derekellis.kgtfs.cli

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption

class MainCommand : NoOpCliktCommand() {
  init {
    versionOption(VERSION)
  }
}

fun main(vararg args: String) {
  MainCommand().subcommands(ImportCommand()).main(args)
}
