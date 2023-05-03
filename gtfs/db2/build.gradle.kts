plugins {
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  implementation(projects.gtfs.csv)
  implementation(libs.exposed.core)
  implementation(libs.exposed.dao)
  implementation(libs.exposed.javaTime)
  implementation(libs.exposed.jdbc)
}
