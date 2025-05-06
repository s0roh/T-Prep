plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.training"
}

dependencies {
    implementation(projects.coreDatabase)
    implementation(projects.coreCommon)
    implementation(projects.corePreferences)
    implementation(projects.coreNetwork)

    implementation(libs.retrofit.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}