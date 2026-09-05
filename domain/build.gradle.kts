plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // Pure Kotlin module — no Android/ARCore/Compose deps here.
    // Keeps business rules (confidence scoring, cost math, template formulas)
    // unit-testable without an emulator.
}
