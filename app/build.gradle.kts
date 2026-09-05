plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.measurear.pro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.measurear.pro"
        minSdk = 24 // ARCore's minimum supported level — see PRD Section 6
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-phase0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-ar"))
    implementation(project(":core-billing"))
    implementation(project(":core-ads"))
    implementation(project(":core-database"))
    implementation(project(":core-export"))
    implementation(project(":feature-measure"))
    implementation(project(":feature-level"))
    implementation(project(":feature-ruler"))
    implementation(project(":feature-converter"))
    implementation(project(":feature-roomplan"))
    implementation(project(":feature-fitchecker"))
    implementation(project(":feature-templates"))
    implementation(project(":feature-paywall"))

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.navigation.compose)
}
