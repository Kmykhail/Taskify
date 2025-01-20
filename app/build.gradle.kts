plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.kote.taskifyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kote.taskifyapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.kote.taskifyapp.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources.excludes.addAll(listOf("META-INF/LICENSE.md", "META-INF/LICENSE", "META-INF/NOTICE", "META-INF/NOTICE.md"))
        resources.merges.addAll(listOf("META-INF/LICENSE.md", "META-INF/LICENSE-notice.md"))
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.common)
    implementation(libs.androidx.material.icons.extended.android)
    implementation(libs.material)
    implementation(libs.androidx.material)

    // Room database
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.gson)

    // Dagger - Hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Hilt testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.47")
    testImplementation("com.google.dagger:hilt-android-testing:2.47")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.47")
    testImplementation("io.mockk:mockk:1.13.7")
    androidTestImplementation("io.mockk:mockk-android:1.13.7")
    testImplementation(libs.kotlinx.coroutines.test)
}