package ca.derekellis.kgtfs.cli

import ca.derekellis.kgtfs.GtfsReader
import ca.derekellis.kgtfs.openAsGtfs
import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import io.ktor.http.URLParserException
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.Path.Companion.toPath
import okio.openZip

class SummaryCommand : CliktCommand(help = "Read a GTFS dataset and output a summary of the contents.") {
  private val uri by argument(
    help = "A URI to a zip or directory containing GTFS data. Can be a local zip file, directory, or URL.",
    completionCandidates = CompletionCandidates.Path,
  )

  override fun run() = runBlocking {
    val remoteZipPath = if (uri.startsWith("http", ignoreCase = true) || uri.startsWith("https", ignoreCase = true)) {
      try {
        downloadZip(Url(uri))
      } catch (_: URLParserException) {
        null
      }
    } else {
      null
    }

    val zipPath = remoteZipPath?.toOkioPath() ?: uri.toPath()
    val gtfsReader = FileSystem.SYSTEM.openZip(zipPath).openAsGtfs()

    basicCountStats(gtfsReader)
  }

  private fun basicCountStats(reader: GtfsReader) {
    val agencies = reader.agency().asSequence().count()
    println("Agencies: $agencies")

    val calendars = reader.calendar().asSequence().count()
    println("Calendars: $calendars")

    val calendarDates = reader.calendarDates().asSequence().count()
    println("Calendar dates: $calendarDates")

    val stops = reader.stops().asSequence().count()
    println("Stops: $stops")

    val routes = reader.routes().asSequence().count()
    println("Routes: $routes")

    val trips = reader.trips().asSequence().count()
    println("Trips: $trips")

    val stopTimes = reader.stopTimes().asSequence().count()
    println("Stop times: $stopTimes")

    val shapes = reader.shapes().asSequence().count()
    println("Shapes: $shapes")
  }
}