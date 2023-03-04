plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.publishing)
}

kotlin {
  explicitApi()
}

dependencies {
  implementation(libs.kotlinx.serialization.core)
}
