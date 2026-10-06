package ca.derekellis.kgtfs

import okio.FileSystem
import okio.ForwardingFileSystem
import okio.Path
import okio.Path.Companion.toPath

object GtfsFileSystem : ForwardingFileSystem(FileSystem.RESOURCES) {
  override fun onPathParameter(path: Path, functionName: String, parameterName: String): Path {
    return "gtfs".toPath() / path
  }
}
