plugins {
    id("android-feature-lib-convention")
    id("android-feature-test-lib-convention")
}

android {
    namespace = "com.example.feature.reminder"
}

dependencies {
    implementation(projects.dataReminder)
    implementation(projects.coreDatabase)
    implementation(projects.coreCommon)
}