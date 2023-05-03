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
include(":raptor")
