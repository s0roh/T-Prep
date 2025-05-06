plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.data.reminder"
}

dependencies {
    implementation(projects.coreDatabase)
    implementation(projects.coreNetwork)
    implementation(projects.corePreferences)

    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)
    implementation(libs.work.runtime.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}