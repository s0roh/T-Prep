plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.data.profile"
}

dependencies {
    implementation(projects.coreNetwork)
    implementation(projects.coreCommon)
    implementation(projects.corePreferences)

    implementation(libs.retrofit.core)
    implementation(libs.coil.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}