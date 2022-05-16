package ca.derekellis.kgtfs.di

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ca.derekellis.kgtfs.db.GtfsDatabase
import ca.derekellis.kgtfs.db.GtfsTimeAdapter
import io.github.dellisd.kgtfs.db.Calendar
import io.github.dellisd.kgtfs.db.CalendarDate
import ca.derekellis.kgtfs.db.InstantColumnAdapter
import ca.derekellis.kgtfs.db.LocationTypeAdapter
import io.github.dellisd.kgtfs.db.Metadata
import ca.derekellis.kgtfs.db.LocalDateAdapter
import io.github.dellisd.kgtfs.db.Route
import ca.derekellis.kgtfs.db.RouteIdAdapter
import ca.derekellis.kgtfs.db.RouteTypeAdapter
import ca.derekellis.kgtfs.db.ServiceIdAdapter
import io.github.dellisd.kgtfs.db.Shape
import ca.derekellis.kgtfs.db.ShapeIdAdapter
import io.github.dellisd.kgtfs.db.Stop
import ca.derekellis.kgtfs.db.StopIdAdapter
import io.github.dellisd.kgtfs.db.StopTime
import io.github.dellisd.kgtfs.db.Trip
import ca.derekellis.kgtfs.db.TripIdAdapter
import ca.derekellis.kgtfs.db.migrateIfNeeded
import ca.derekellis.kgtfs.domain.GtfsLoader
import ca.derekellis.kgtfs.dsl.MutableStaticGtfsScope
import ca.derekellis.kgtfs.dsl.StaticGtfsScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ScriptScope
internal abstract class ScriptComponent(private val dbPath: String = "gtfs.db") {
    @Provides
    @ScriptScope
    fun provideDatabase(): GtfsDatabase = JdbcSqliteDriver("jdbc:sqlite:$dbPath").let { driver ->
        migrateIfNeeded(driver)
        GtfsDatabase(
            driver,
            StopAdapter = Stop.Adapter(
                StopIdAdapter,
                LocationTypeAdapter
            ),
            StopTimeAdapter = StopTime.Adapter(
                TripIdAdapter,
                GtfsTimeAdapter,
                GtfsTimeAdapter,
                StopIdAdapter,
                IntColumnAdapter,
                IntColumnAdapter,
                IntColumnAdapter
            ),
            TripAdapter = Trip.Adapter(
                RouteIdAdapter,
                ServiceIdAdapter,
                TripIdAdapter,
                IntColumnAdapter,
                ShapeIdAdapter
            ),
            MetadataAdapter = Metadata.Adapter(InstantColumnAdapter),
            CalendarAdapter = Calendar.Adapter(
                ServiceIdAdapter,
                LocalDateAdapter,
                LocalDateAdapter
            ),
            CalendarDateAdapter = CalendarDate.Adapter(
                ServiceIdAdapter,
                LocalDateAdapter, IntColumnAdapter),
            RouteAdapter = Route.Adapter(RouteIdAdapter, RouteTypeAdapter),
            ShapeAdapter = Shape.Adapter(ShapeIdAdapter, IntColumnAdapter)
        )
    }

    abstract val gtfsLoader: GtfsLoader

    abstract fun taskDsl(): StaticGtfsScope

    abstract fun mutableTaskDsl(): MutableStaticGtfsScope
}
