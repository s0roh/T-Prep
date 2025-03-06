plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.profile"
}

dependencies {
    implementation(project(":data-history"))
    implementation(project(":core-preferences"))

    implementation(libs.android.image.cropper)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}