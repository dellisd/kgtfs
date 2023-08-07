package ca.derekellis.kgtfs.raptor.db

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.slf4j.LoggerFactory
import java.util.Properties

internal fun getDatabase(path: String, readOnly: Boolean = false): RaptorDatabase =
  JdbcSqliteDriver(
    "jdbc:sqlite:$path",
    Properties().apply {
      if (readOnly) setProperty("open_mode", "1")
    },
  ).let { driver ->
    migrateIfNeeded(driver)
    RaptorDatabase(
      driver,
      RouteAdapter = Route.Adapter(RouteIdAdapter),
      StopAdapter = Stop.Adapter(StopIdAdapter),
      StopTimeAdapter = StopTime.Adapter(
        TripIdAdapter,
        StopIdAdapter,
        GtfsTimeAdapter,
        IntColumnAdapter,
      ),
      TripAdapter = Trip.Adapter(TripIdAdapter, RouteIdAdapter),
      RouteAtStopAdapter = RouteAtStop.Adapter(StopIdAdapter, RouteIdAdapter, IntColumnAdapter),
      TransferAdapter = Transfer.Adapter(StopIdAdapter, StopIdAdapter, FeatureAdapter),
    )
  }

private const val versionPragma = "user_version"

internal fun migrateIfNeeded(driver: JdbcSqliteDriver) {
  val logger = LoggerFactory.getLogger("migrateIfNeeded")
  val oldVersion =
    driver.executeQuery(null, "PRAGMA $versionPragma", mapper = { cursor ->
      val result = if (cursor.next().value) {
        cursor.getLong(0)
      } else {
        null
      }
      QueryResult.Value(result)
    }, 0).value ?: 0L

  val newVersion = RaptorDatabase.Schema.version

  if (oldVersion == 0L) {
    logger.info("Creating DB version $newVersion!")
    RaptorDatabase.Schema.create(driver)
    driver.execute(null, "PRAGMA $versionPragma=$newVersion", 0)
  } else if (oldVersion < newVersion) {
    logger.info("Migrating DB from version $oldVersion to $newVersion!")
    RaptorDatabase.Schema.migrate(driver, oldVersion, newVersion)
    driver.execute(null, "PRAGMA $versionPragma=$newVersion", 0)
  }
}
