import com.vanniktech.maven.publish.MavenPublishPluginExtension
import com.vanniktech.maven.publish.SonatypeHost

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
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.spatialk.turf)
    implementation(libs.rtree)
    implementation(libs.logback)
    implementation(libs.bundles.sqldelight)
    implementation(libs.bundles.ktor.client)
    implementation(project(":gtfs"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn", "-opt-in=io.github.dellisd.spatialk.turf.ExperimentalTurfApi")
}

sqldelight {
    database("RaptorDatabase") {
        packageName = "ca.derekellis.kgtfs.raptor.db"
    }
}

extensions.getByType<MavenPublishPluginExtension>().apply {
    sonatypeHost = SonatypeHost.S01
}
