# kgtfs

A Kotlin library for working with static [GTFS](https://developers.google.com/transit/gtfs) datasets.

```kotlin
dependencies {
  implementation("ca.derekellis.kgtfs:kgtfs:0.5.0")
}
```

## Reading GTFS Data

To read a GTFS dataset, open a reader:
```kotlin
val reader: GtfsReader = FileSystem.SYSTEM
  .openZip("/path/to/gtfs.zip")
  .openAsGtfs()
```

Using this reader, you can read from each file by iterating over the file:

```kotlin
reader.stops().use { stopsReader ->
  stops.asSequence().forEach { 
    // Access stop data here
  }
}
```

## Database Extension

```kotlin
dependencies {
  implementation("ca.derekellis.kgtfs:kgtfs-db:0.5.0")
}
```

You can use a `GtfsReader` to populate a SQLite database for more efficient querying. KGTFS uses Exposed to provide a
simple way to import a dataset and query the database directly.

```kotlin
val db = GtfsDb.fromReader(gtfsReader, "path/to/db")

// in-memory database
val db = GtfsDb.fromReader(gtfsReader, ":memory:")

// pre-existing database
val db = GtfsDb.open("gtfs.db")
```

### Querying GTFS Data

KGTFS exposes a DSL based on [Exposed](https://github.com/jetbrains/Exposed/) to query raw GTFS data. The tables of the
database can be accessed within the `query { }` method block.

```kotlin
val gtfs = GtfsDb.open(Path("gtfs.db"))

val allStops = gtfs.query { Stops.selectAll().map(Stops.Mapper) }
// [Stop(...), Stop(...), ...]
```

A number of common queries and algorithms are also available as extensions.

```kotlin
// Range of dates that the GTFS dataset covers
val dataRange: ClosedRange<LocalDate> = gtfs.query { serviceRange() }

// List of Calendars for the current date
val calendarsToday: Set<Calendar> = gtfs.query { Calendars.today() }
```


## Command Line
You can also use the KGTFS command line interface to import a GTFS dataset.
```shell
# Usage: main import [OPTIONS] URI
# 
#   Import a GTFS dataset to a kgtfs-compatible SQLite database.
# 
# Options:
#   -o, --output PATH
#   -h, --help         Show this message and exit
# 
# Arguments:
#   URI  A URI to a zip or directory containing GTFS data. Can be a local zip
#        file, directory, or URL.

# e.g.
kgtfs import https://www.octranspo.com/files/google_transit.zip -o gtfs.db
```
