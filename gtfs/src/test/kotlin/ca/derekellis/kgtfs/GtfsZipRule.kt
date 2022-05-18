package ca.derekellis.kgtfs

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.div

class GtfsZipRule : TestRule {
    lateinit var zip: Path
        private set

    private val files = listOf(
        "agency.txt",
        "calendar.txt",
        "calendar_dates.txt",
        "routes.txt",
        "shapes.txt",
        "stop_times.txt",
        "stops.txt",
        "trips.txt"
    )

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val tempDir = Files.createTempDirectory("gtfs_")
                zip = tempDir / "gtfs.zip"
                val zipOut = ZipOutputStream(FileOutputStream(zip.toString()))

                files.forEach { file ->
                    val toZip = File("src/test/resources/gtfs/$file")
                    zipOut.putNextEntry(ZipEntry(toZip.name))
                    Files.copy(toZip.toPath(), zipOut)
                }
                zipOut.close()

                base.evaluate()
            }
        }
    }
}
