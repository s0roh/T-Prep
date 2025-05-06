plugins {
    id("android-feature-lib-convention")
    id("android-feature-test-lib-convention")
}

android {
    namespace = "com.example.feature.profile"
}

dependencies {
    implementation(projects.dataHistory)
    implementation(projects.dataProfile)
    implementation(projects.dataDecks)
    implementation(projects.corePreferences)
    implementation(projects.coreCommon)
    implementation(projects.coreDatabase)

    implementation(libs.android.image.cropper)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}