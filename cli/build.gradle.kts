plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "io.github.dellisd.kgtfs"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.bundles.kotlin.scripting)
    implementation(libs.clikt)
    implementation(libs.logback)
    implementation(project(":gtfs"))
}
