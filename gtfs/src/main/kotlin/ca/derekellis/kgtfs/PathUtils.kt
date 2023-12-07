package ca.derekellis.kgtfs

import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile

private val ZIP_SIGNATURE = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
private val EMPTY_ZIP_SIGNATURE = byteArrayOf(0x50, 0x4B, 0x05, 0x06)
private val SQLITE_SIGNATURE = byteArrayOf(
  0x53, 0x51, 0x4C, 0x69, 0x74, 0x65, 0x20, 0x66,
  0x6F, 0x72, 0x6D, 0x61, 0x74, 0x20, 0x33, 0x00,
)

internal fun Path.isZipFile(): Boolean {
  if (!isRegularFile()) return false
  val bytes = inputStream().use { stream ->
    stream.readNBytes(ZIP_SIGNATURE.size)
  }

  return bytes.contentEquals(ZIP_SIGNATURE) || bytes.contentEquals(EMPTY_ZIP_SIGNATURE)
}

internal fun Path.isSqliteFile(): Boolean {
  if (!isRegularFile()) return false
  val bytes = inputStream().use { stream ->
    stream.readNBytes(SQLITE_SIGNATURE.size)
  }

  return bytes.contentEquals(SQLITE_SIGNATURE)
}
