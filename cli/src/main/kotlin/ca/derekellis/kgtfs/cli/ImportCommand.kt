package ca.derekellis.kgtfs.cli

import ca.derekellis.kgtfs.GtfsDb
import ca.derekellis.kgtfs.io.GtfsReader
import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.URLParserException
import io.ktor.http.Url
import io.ktor.utils.io.jvm.javaio.copyTo
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
    completionCandidates = CompletionCandidates.Path,
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
    } else {
      null
    }

    GtfsDb.fromReader(GtfsReader(remoteZipPath ?: Path(uri)), output)
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
