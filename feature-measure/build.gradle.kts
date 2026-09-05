plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.measurear.pro.feature.measure"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-ar"))
    implementation(project(":core-database"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    // Needed for rememberLauncherForActivityResult (runtime CAMERA permission request)
    implementation(libs.activity.compose)
    // ContextCompat.checkSelfPermission
    implementation(libs.core.ktx)
    implementation(libs.sceneview)
}
