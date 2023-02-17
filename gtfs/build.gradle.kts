import com.vanniktech.maven.publish.MavenPublishPluginExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    alias(libs.plugins.publishing)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    explicitApi()

    sourceSets.main {
        kotlin.srcDir("$buildDir/generated/ksp/main/kotlin")
    }
}

dependencies {
    ksp(libs.kotlin.inject.compiler)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.sqldelight)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.csv)
    implementation(libs.logback)

    api(libs.spatialk.turf)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}

sqldelight {
    databases {
        create("GtfsDatabase") {
            packageName.set("ca.derekellis.kgtfs.db")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-opt-in=kotlin.contracts.ExperimentalContracts"
    )
}

extensions.getByType<MavenPublishPluginExtension>().apply {
    sonatypeHost = SonatypeHost.S01
}
