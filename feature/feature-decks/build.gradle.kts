plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.decks"
}

dependencies {
    implementation(projects.dataDecks)
    implementation(projects.dataHistory)
    implementation(projects.dataTraining)
    implementation(projects.dataLocalDecks)
    implementation(projects.coreCommon)
    implementation(projects.coreDatabase)
    implementation(projects.corePreferences)

    implementation (libs.compose.charts)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
}