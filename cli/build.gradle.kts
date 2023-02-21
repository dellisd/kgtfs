plugins {
  alias(libs.plugins.kotlin.jvm)
  application
}

group = property("GROUP")!!
version = property("VERSION_NAME")!!

repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation(libs.bundles.kotlin.scripting)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.bundles.ktor.client)
  implementation(libs.clikt)
  implementation(libs.logback)
  implementation(project(":gtfs"))
  implementation(project(":raptor"))
}

application {
  mainClass.set("ca.derekellis.kgtfs.cli.MainKt")
}

kotlin {
  sourceSets {
    val main by getting {
      kotlin.srcDir("$buildDir/generated/kotlin")
    }
  }
}

val versionInfo = tasks.register("versionInfo") {
  val outputDir = buildDir.resolve("generated/kotlin")
  inputs.property("version", version)
  outputs.dir(outputDir)

  doLast {
    val versionFile = file("$outputDir/ca/derekellis/kgtfs/cli/Version.kt")
    versionFile.parentFile.mkdirs()
    versionFile.writeText("""
      |// Generated file. Do not edit!
      |package ca.derekellis.kgtfs.cli
      |
      |const val VERSION = "${project.version}"
      |
    """.trimMargin())
  }
}

tasks.named("compileKotlin").configure {
  dependsOn(versionInfo)
}
