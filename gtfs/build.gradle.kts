plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.publishing)
  `java-test-fixtures`
}

kotlin {
  explicitApi()

  compilerOptions {
    freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")
  }
}

dependencies {
  api(libs.okio)

  implementation(libs.okhttp)
  implementation(libs.bundles.ktor.client)
  implementation(libs.csv)

  api(libs.spatialk.turf)

  testImplementation(libs.junit)
  testImplementation(libs.truth)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.okio.fakefilesystem)
  testImplementation(libs.assertk)

  testFixturesApi(libs.junit)
}
