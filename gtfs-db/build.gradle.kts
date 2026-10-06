plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.publishing)
}

kotlin {
  explicitApi()
}

dependencies {
  api(project(":gtfs"))
  api(libs.exposed.core)
  api(libs.exposed.javaTime)
  api(libs.exposed.jdbc)

  implementation(libs.sqlite)

  testImplementation(testFixtures(project(":gtfs")))
  testImplementation(libs.truth)
}
