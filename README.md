# kgtfs

A Kotlin DSL and scripting utility for working with static [GTFS](https://developers.google.com/transit/gtfs) datasets.

TODO: More documentation!

## Example

From [`scripts/test.gtfs.kts`](scripts/test.gtfs.kts)

```kotlin
gtfs(source = "https://www.octranspo.com/files/google_transit.zip") {
    val stop = stops.getByCode("7225").first()

    stop.routes.forEach {
        println("Route: ${it.id}")
    }

    val trip = stop.trips.first()
    println("Trip: ${trip.id}, Route: ${trip.route.shortName}")
    trip.stops.forEach {
        println("Stop: ${it.name}")
    }

    println(calendar.today().map { it.service_id })
}

// Reusable gtfs container
val gtfs = runBlocking { Gtfs(source =  "https://www.octranspo.com/files/google_transit.zip") }
gtfs {
    // Use static GTFS data in here
}

// Make edits to GTFS data
gtfs.edit {
    stops.add(Stop(/* ... */))
}

// Export GTFS CSV files
gtfs.exportCSV("/gtfs_dir")
```

# Documentation

## Loading GTFS

GTFS data can be loaded using the `Gtfs()` constructor function.
Data can be loaded from a URL, a compressed zip archive, a directory, or from a SQLite database.

```kotlin
// From URL
Gtfs(source = "https://www.octranspo.com/files/google_transit.zip")

// From .zip file
Gtfs(source = "./google_transit.zip")

// From directory (extracted from the .zip archive)
Gtfs(source = "./google_transit")

// From SQLite database
Gtfs(source = "./gtfs.db")
```

The data is loaded and indexed into a SQLite database for efficient processing.
By default, an in-memory database is used which means that the data needs to be extracted/loaded each time the `Gtfs()` function is called.

The `dbPath` argument can be passed to specify a file to write the database to.
If the database file exists then it will be used in subsequent times when the `Gtfs()` function is called.
This means the data will not be re-extracted which can save a lot of time.
If new data from the source is available, then the database will be updated.
