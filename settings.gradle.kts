rootProject.name = "kgtfs"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":cli")
include(":graph")
include(":gtfs")
include(":gtfs-realtime")
include(":raptor")
