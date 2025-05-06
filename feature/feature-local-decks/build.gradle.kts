plugins {
    id("android-feature-lib-convention")
    id("android-feature-test-lib-convention")
}

android {
    namespace = "com.example.feature.localdecks"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.dataLocalDecks)

    implementation(libs.android.image.cropper)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.work.runtime.ktx)
}