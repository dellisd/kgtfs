plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.publishing) apply false
}

repositories {
    mavenCentral()
    google()
}
