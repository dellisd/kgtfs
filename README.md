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
```