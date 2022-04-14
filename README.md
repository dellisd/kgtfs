# kgtfs

A Kotlin DSL and scripting utility for working with static [GTFS](https://developers.google.com/transit/gtfs) datasets.

TODO: More documentation!

## Example

From [`scripts/test.gtfs.kts`](scripts/test.gtfs.kts)

```kotlin
kgtfs {
    gtfs("https://www.octranspo.com/files/google_transit.zip")

    task {
        val stop = stops.getByCode("7225").first()

        // Print every route at the selected stop
        stop.routes.forEach {
            println("Route: ${it.id}")
        }

        // Get the sequence of stops for a route from the selected stop
        val trip = stop.trips.first()
        println("Trip: ${trip.id}, Route: ${trip.route.shortName}")
        trip.stops.forEach {
            println("Stop: ${it.name}")
        }

        // Get all of today's Calendar service ids
        println(calendar.today().map { it.service_id })
    }
}
```