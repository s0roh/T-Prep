plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.auth"
}

dependencies {
    implementation(projects.coreNetwork)
    implementation(projects.corePreferences)
    implementation(projects.dataLocalDecks)

    implementation(libs.retrofit.core)
}