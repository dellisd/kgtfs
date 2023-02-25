plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.sqldelight)
}

kotlin {
  explicitApi()
}

dependencies {
  implementation(project(":gtfs:csv"))

  implementation(libs.sqldelight.driver.sqlite)
  implementation(libs.sqldelight.adapters)
}

sqldelight {
  databases {
    create("GtfsDatabase") {
      packageName.set("ca.derekellis.kgtfs.db")
    }
  }
}
