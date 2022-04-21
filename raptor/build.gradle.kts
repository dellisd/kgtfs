plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.spatialk.turf)
    implementation(libs.rtree)
    implementation(libs.logback)
    implementation(libs.bundles.sqldelight)
    implementation(project(":gtfs"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn", "-opt-in=io.github.dellisd.spatialk.turf.ExperimentalTurfApi")
}

sqldelight {
    database("RaptorDatabase") {
        packageName = "io.github.dellisd.raptor.db"
    }
}
