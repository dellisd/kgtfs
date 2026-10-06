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

  implementation(libs.exposed.javaTime)
  implementation(libs.exposed.jdbc)
  implementation(libs.sqlite)

  testImplementation(testFixtures(project(":gtfs")))
  testImplementation(libs.truth)
}
