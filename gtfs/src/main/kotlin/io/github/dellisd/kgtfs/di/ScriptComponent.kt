package io.github.dellisd.kgtfs.di

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.dellisd.kgtfs.db.Calendar
import io.github.dellisd.kgtfs.db.CalendarDate
import io.github.dellisd.kgtfs.db.GtfsDatabase
import io.github.dellisd.kgtfs.db.InstantColumnAdapter
import io.github.dellisd.kgtfs.db.LocationTypeAdapter
import io.github.dellisd.kgtfs.db.Metadata
import io.github.dellisd.kgtfs.db.LocalDateAdapter
import io.github.dellisd.kgtfs.db.Route
import io.github.dellisd.kgtfs.db.RouteIdAdapter
import io.github.dellisd.kgtfs.db.RouteTypeAdapter
import io.github.dellisd.kgtfs.db.ServiceIdAdapter
import io.github.dellisd.kgtfs.db.Shape
import io.github.dellisd.kgtfs.db.Stop
import io.github.dellisd.kgtfs.db.StopIdAdapter
import io.github.dellisd.kgtfs.db.StopTime
import io.github.dellisd.kgtfs.db.Trip
import io.github.dellisd.kgtfs.db.TripIdAdapter
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
                StopIdAdapter,
                LocationTypeAdapter
            ),
            StopTimeAdapter = StopTime.Adapter(
                TripIdAdapter,
                StopIdAdapter,
                IntColumnAdapter,
                IntColumnAdapter,
                IntColumnAdapter
            ),
            TripAdapter = Trip.Adapter(RouteIdAdapter, ServiceIdAdapter, TripIdAdapter, IntColumnAdapter),
            MetadataAdapter = Metadata.Adapter(InstantColumnAdapter),
            CalendarAdapter = Calendar.Adapter(ServiceIdAdapter, LocalDateAdapter, LocalDateAdapter),
            CalendarDateAdapter = CalendarDate.Adapter(ServiceIdAdapter, LocalDateAdapter, IntColumnAdapter),
            RouteAdapter = Route.Adapter(RouteIdAdapter, RouteTypeAdapter),
            ShapeAdapter = Shape.Adapter(TripIdAdapter, IntColumnAdapter)
        )
    }

    abstract val gtfsLoader: GtfsLoader

    abstract fun taskDsl(): TaskDsl
}
