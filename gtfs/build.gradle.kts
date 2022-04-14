plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
}

group = "io.github.dellisd.kgtfs"
version = "0.1.0-SNAPSHOT"

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
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.csv)
    implementation(libs.logback)
}

sqldelight {
    database("GtfsDatabase") {
        packageName = "io.github.dellisd.kgtfs.db"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}
