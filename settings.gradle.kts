rootProject.name = "kgtfs"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

include(":cli")
include(":gtfs")
include(":gtfs:csv")
include(":gtfs:db")
include(":raptor")
