package ca.derekellis.kgtfs.cli

import ca.derekellis.kgtfs.io.GtfsReader
import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.outputStream

class ImportCommand : CliktCommand(help = "Import a GTFS dataset to a kgtfs-compatible SQLite database.") {
  private val uri by argument(
    help = "A URI to a zip or directory containing GTFS data. Can be a local zip file, directory, or URL.",
    completionCandidates = CompletionCandidates.Path
  )

  private val output by option("--output", "-o")
    .path(canBeDir = false)
    .default(Path("gtfs.db"))

  override fun run(): Unit = runBlocking {
    if (output.exists()) {
      confirm("The output target $output already exists. Overwrite?", abort = true)
    }

    val remoteZipPath = if (uri.startsWith("http", ignoreCase = true) || uri.startsWith("https", ignoreCase = true)) {
      try {
        downloadZip(Url(uri))
      } catch (_: URLParserException) {
        null
      }
    } else null

    GtfsReader(remoteZipPath ?: Path(uri))
      .intoCache(output)
      .close()
  }

  private suspend fun downloadZip(url: Url, onProgress: (Int) -> Unit = {}): Path = withContext(Dispatchers.IO) {
    val tempPath = Files.createTempFile("kgtfs", null)
    val client = HttpClient()

    val response = client.get(url) {
      onDownload { bytesSentTotal, contentLength ->
        val percent = ((bytesSentTotal.toDouble() / contentLength) * 100).toInt()
        onProgress(percent)
      }
    }
    tempPath.outputStream().use { stream ->
      response.bodyAsChannel().copyTo(stream)
    }

    return@withContext tempPath
  }
}
