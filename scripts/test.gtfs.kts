kgtfs {
    gtfs("https://www.octranspo.com/files/google_transit.zip")

    task {
        val stop = stops.getByCode("7225").first()

        stop.routes.forEach {
            println("Route: ${it.route_id}")
        }

        val trip = stop.trips.first()
        println("Trip: ${trip.trip_id}, Route: ${trip.route.route_short_name}")
        trip.stops.forEach {
            println("Stop: ${it.stop_name}")
        }

        println(calendar.today().map { it.service_id })
    }
}
