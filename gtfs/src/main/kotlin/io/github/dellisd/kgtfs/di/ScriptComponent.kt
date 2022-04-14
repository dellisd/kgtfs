package io.github.dellisd.kgtfs.di

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.dellisd.kgtfs.db.CalendarDate
import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.InstantColumnAdapter
import io.github.dellisd.kgtfs.db.Metadata
import io.github.dellisd.kgtfs.db.Route
import io.github.dellisd.kgtfs.db.Stop
import io.github.dellisd.kgtfs.db.StopTime
import io.github.dellisd.kgtfs.db.Trip
import io.github.dellisd.kgtfs.db.migrateIfNeeded
import io.github.dellisd.kgtfs.domain.GtfsLoader
import io.github.dellisd.kgtfs.dsl.TaskDsl
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ScriptScope
internal abstract class ScriptComponent(private val dbPath: String = "gtfs.db") {
    @Provides
    @ScriptScope
    fun provideDatabase(): GtfsDatabase = JdbcSqliteDriver("jdbc:sqlite:$dbPath").use { driver ->
        migrateIfNeeded(driver)
        GtfsDatabase(
            driver,
            StopAdapter = Stop.Adapter(
                IntColumnAdapter
            ),
            StopTimeAdapter = StopTime.Adapter(IntColumnAdapter, IntColumnAdapter, IntColumnAdapter),
            TripAdapter = Trip.Adapter(IntColumnAdapter),
            MetadataAdapter = Metadata.Adapter(InstantColumnAdapter),
            CalendarDateAdapter = CalendarDate.Adapter(IntColumnAdapter),
            RouteAdapter = Route.Adapter(IntColumnAdapter)
        )
    }

    abstract val gtfsLoader: GtfsLoader

    abstract fun taskDsl(): TaskDsl
}
