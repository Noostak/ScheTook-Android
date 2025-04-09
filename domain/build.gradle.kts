plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)

    id("kotlin-parcelize")
}

android {
    namespace = "com.sopt.domain"
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17) // Java와 동일한 JVM 버전 사용
}

dependencies {
    // KotlinDependencies
    implementation(libs.kotlin)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.collections)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Hilt
    ksp(libs.hilt.compiler)
    implementation(libs.javax.inject)
    implementation(libs.hilt.core)
}
