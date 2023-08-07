plugins {
  alias(libs.plugins.kotlin.jvm)
}

dependencies {
  api(project(":gtfs"))

  implementation(libs.jgrapht.core)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
  testImplementation(libs.jgrapht.io)
}
