import com.vanniktech.maven.publish.MavenPublishPluginExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.publishing)
}

kotlin {
    explicitApi()

    sourceSets.main {
        kotlin.srcDir("$buildDir/generated/ksp/main/kotlin")
    }
}

dependencies {
    api(project(":gtfs:csv"))
    implementation(project(":gtfs:db"))

    ksp(libs.kotlin.inject.compiler)
    implementation(libs.kotlin.inject.runtime)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.sqldelight)
    implementation(libs.csv)

    api(libs.spatialk.turf)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-opt-in=kotlin.contracts.ExperimentalContracts"
    )
}
