package ca.derekellis.kgtfs.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.slf4j.LoggerFactory

private const val versionPragma = "user_version"

internal fun migrateIfNeeded(driver: JdbcSqliteDriver) {
    val logger = LoggerFactory.getLogger("migrateIfNeeded")
    val oldVersion =
        driver.executeQuery(null, "PRAGMA $versionPragma", mapper = { cursor ->
            if (cursor.next()) {
                cursor.getLong(0)?.toInt()
            } else {
                null
            }
        }, 0) ?: 0

    val newVersion = GtfsDatabase.Schema.version

    if (oldVersion == 0) {
        logger.info("Creating DB version $newVersion!")
        GtfsDatabase.Schema.create(driver)
        driver.execute(null, "PRAGMA $versionPragma=$newVersion", 0)
    } else if (oldVersion < newVersion) {
        logger.info("Migrating DB from version $oldVersion to $newVersion!")
        GtfsDatabase.Schema.migrate(driver, oldVersion, newVersion)
        driver.execute(null, "PRAGMA $versionPragma=$newVersion", 0)
    }
}
