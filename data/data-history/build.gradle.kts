plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.history"
}

dependencies {
    implementation(projects.coreDatabase)
    implementation(projects.corePreferences)
    implementation(projects.coreNetwork)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}