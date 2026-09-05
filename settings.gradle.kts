pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MeasureARPro"

include(
    ":app",
    ":domain",
    ":core-ar",
    ":core-billing",
    ":core-ads",
    ":core-database",
    ":core-export",
    ":feature-measure",
    ":feature-level",
    ":feature-ruler",
    ":feature-converter",
    ":feature-roomplan",
    ":feature-fitchecker",
    ":feature-templates",
    ":feature-paywall"
)
