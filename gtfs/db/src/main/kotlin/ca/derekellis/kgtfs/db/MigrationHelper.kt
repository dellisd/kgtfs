package ca.derekellis.kgtfs.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

private const val versionPragma = "user_version"

public fun migrateIfNeeded(driver: JdbcSqliteDriver) {
    val oldVersion =
        driver.executeQuery(null, "PRAGMA $versionPragma", mapper = { cursor ->
            if (cursor.next()) {
                cursor.getLong(0)?.toInt()
            } else {
                null
            }
        }, 0).value ?: 0

    val newVersion = GtfsDatabase.Schema.version

    check(oldVersion <= newVersion) { "Database version $oldVersion is newer than schema version $newVersion" }

    if (oldVersion == 0) {
        GtfsDatabase.Schema.create(driver)
        driver.execute(null, "PRAGMA $versionPragma=$newVersion", 0)
    } else if (oldVersion < newVersion) {
        GtfsDatabase.Schema.migrate(driver, oldVersion, newVersion)
        driver.execute(null, "PRAGMA $versionPragma=$newVersion", 0)
    }
}
