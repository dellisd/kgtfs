package ca.derekellis.kgtfs.cli

import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.outputStream

internal suspend fun downloadZip(url: Url, onProgress: (Int) -> Unit = {}): Path = withContext(Dispatchers.IO) {
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
