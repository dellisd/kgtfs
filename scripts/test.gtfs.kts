kgtfs {
    gtfs("https://www.octranspo.com/files/google_transit.zip")

    task {
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
}
