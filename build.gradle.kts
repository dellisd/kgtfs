plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.publishing) apply false
  alias(libs.plugins.spotless)
}

repositories {
  mavenCentral()
  google()
}

spotless {
  kotlin {
    target("**/*.kt", "**/*.kts")
    targetExclude("**/build/generated/**/*.*")

    ktlint(libs.versions.ktlint.get()).editorConfigOverride(
      mapOf(
        "indent_size" to "2",
        "ktlint_package-name" to "disabled",
      ),
    )
    trimTrailingWhitespace()
    endWithNewline()
  }
}
