plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.publishing)
}

kotlin {
  explicitApi()

  compilerOptions {
    freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")
  }
}

dependencies {
  api(libs.exposed.core)

  implementation(libs.okhttp)
  implementation(libs.bundles.ktor.client)
  implementation(libs.bundles.sqldelight)
  implementation(libs.csv)

  implementation(libs.exposed.core)
  implementation(libs.exposed.javaTime)
  implementation(libs.exposed.jdbc)

  api(libs.spatialk.turf)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
  testImplementation(libs.kotlinx.coroutines.test)
}
