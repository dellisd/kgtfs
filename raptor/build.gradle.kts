plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.publishing)
}

repositories {
  mavenCentral()
  google()
}

kotlin {
  explicitApi()

  compilerOptions {
    freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn", "-opt-in=io.github.dellisd.spatialk.turf.ExperimentalTurfApi")
  }
}

dependencies {
  api(projects.gtfs)

  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.spatialk.turf)
  implementation(libs.rtree)
  implementation(libs.logback)
  implementation(libs.bundles.sqldelight)
  implementation(libs.bundles.ktor.client)
}

sqldelight {
  databases {
    create("RaptorDatabase") {
      packageName.set("ca.derekellis.kgtfs.raptor.db")
    }
  }
}
