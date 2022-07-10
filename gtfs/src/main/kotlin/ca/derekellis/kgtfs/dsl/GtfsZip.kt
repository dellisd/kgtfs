package ca.derekellis.kgtfs.dsl

import io.ktor.http.Url
import java.io.File
import java.nio.file.Path

public sealed class GtfsZip {
    public data class Local(val path: Path) : GtfsZip()
    public data class Remote(val url: Url) : GtfsZip()
    public data class LocalSqliteFile(val path: Path) : GtfsZip()
    public data class LocalDirectory(val path: Path) : GtfsZip()
}
