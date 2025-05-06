plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.decks"
}

dependencies {
    implementation(projects.coreNetwork)
    implementation(projects.coreDatabase)
    implementation(projects.corePreferences)
    implementation(projects.coreCommon)

    implementation(libs.retrofit.core)

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}