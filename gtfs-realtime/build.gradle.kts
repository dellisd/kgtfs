plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.publishing)
  alias(libs.plugins.wire)
}

kotlin {
  explicitApi()
}

wire {
  kotlin {}
}
