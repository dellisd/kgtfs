package io.github.dellisd.kgtfs.dsl

import io.ktor.http.Url
import java.io.File

public sealed class GtfsZip {
    public data class Local(val file: File) : GtfsZip()
    public data class Remote(val url: Url) : GtfsZip()
}
