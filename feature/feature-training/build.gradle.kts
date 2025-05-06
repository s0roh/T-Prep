plugins {
    id("android-feature-lib-convention")
    id("android-feature-test-lib-convention")
}

android {
    namespace = "com.example.feature.training"
}

dependencies {
    implementation(projects.coreCommon)
    implementation(projects.coreDatabase)
    implementation(projects.dataTraining)
    implementation(projects.dataDecks)
    implementation(projects.dataLocalDecks)
    implementation(projects.dataProfile)

    implementation (libs.androidx.foundation)
    implementation(libs.konfetti.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}