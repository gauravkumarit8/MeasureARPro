// Root build file — declares plugin versions once (sourced from
// gradle/libs.versions.toml, the single source of truth), applied `false`
// here, applied per-module in each module's own build.gradle.kts.
//
// NOTE: this previously hardcoded "8.6.0" directly here, duplicating (and
// silently drifting from) the AGP version in the catalog — bumping the
// catalog alone would NOT have updated the actual build. Fixed to reference
// the catalog aliases so there's exactly one place to change versions.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
