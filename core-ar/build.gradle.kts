plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.measurear.pro.core.ar"
    compileSdk = 35
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.arcore)
    // SceneView wraps Filament + ARCore for Compose-friendly 3D rendering.
    // Used by Fit Checker (virtual object placement) and Room Planner (wall visualization).
    // Sceneform (Google's original library) is deprecated — do not use it. See PRD Section 6.
    implementation(libs.sceneview)
}
