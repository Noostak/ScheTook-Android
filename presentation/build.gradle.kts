plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.sopt.presentation"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))

    // Compose
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3.compose)
    implementation(libs.constraintlayout.compose)

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.collections)

    // AndroidX Core
    implementation(libs.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)
    implementation(libs.accompanist.insets)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.compose)

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.javax.inject)
    implementation(libs.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.testing)

    // Retrofit (Networking)
    implementation(libs.bundles.retrofit)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.bundles.landscapist.glide)

    // Logging
    implementation(libs.timber)

    // DataStore (Local Storage)
    implementation(libs.bundles.datastore)

    // ViewPager Indicator
    implementation(libs.viewpager.indicator)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Accompanist System UI Controller
    implementation(libs.accompanist.systemuicontroller)

    // Kakao
    implementation(libs.kakao.all)
    implementation(libs.kakao.user)

    // Google
    implementation(libs.play.services.auth)
    implementation(libs.google.id)
    implementation(libs.credentials.core)
    implementation(libs.credentials.play.services.auth)
}
