plugins {
    id("android-feature-lib-convention")
    id("android-feature-test-lib-convention")
}

android {
    namespace = "com.example.feature.history"
}

dependencies {
    implementation(projects.dataHistory)
    implementation(projects.dataDecks)
    implementation(projects.coreCommon)
    implementation(projects.coreDatabase)
}