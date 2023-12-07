# kgtfs

A Kotlin DSL for working with static [GTFS](https://developers.google.com/transit/gtfs) datasets.

## Reading GTFS Data

KGTFS reads GTFS data into a SQLite database to enable more efficient queries.
GTFS data is read using the `GtfsReader` class, which is then used to populate a `GtfsDb` instance.

This will read the GTFS data into the database and save it into the specified path. An in-memory database can be created
by passing `null` into the second parameter. A pre-existing database can be opened using the `open()` method.

```kotlin
val gtfs = GtfsDb.fromReader(GtfsReader.newDirectoryReader(Path("path/to/gtfs")), path = Path("gtfs.db"))

// in-memory database
val gtfs = GtfsDb.fromReader(GtfsReadernewDirectoryReader(Path("path/to/gtfs")), path = ":memory:")

// pre-existing database
val gtfs = GtfsDb.open(Path("gtfs.db"))
```

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

## Querying GTFS Data

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

## Writing GTFS Data

You can create a writer to output the CSV files that make up a GTFS dataset.
A `GtfsDb` can be written into a `GtfsWriter`.

**Writers must be closed to ensure that the CSV files are correctly written to the ZIP archive!!**

```kotlin
val gtfs = GtfsDb.open(Path("gtfs.db"))

val writer = GtfsWriter.newZipWriter(Path("output.zip"))
gtfs.intoWriter(writer)
// Remember to close the writer!
writer.close()
```

You can also make use of the `use` extension to ensure the writer is closed:
```kotlin
GtfsWriter.newZipWriter(Path("output.zip")).use { writer ->
  gtfs.intoWriter(writer)
} // Writer is automatically closed after being used
```
