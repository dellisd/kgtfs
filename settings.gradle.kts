rootProject.name = "kgtfs"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":cli")
include(":gtfs")
include(":gtfs:csv")
include(":gtfs:db")
include(":gtfs:db2")
include(":raptor")
