plugins {
    id("android-base-lib-convention")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.network"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.logging.interceptor)
}