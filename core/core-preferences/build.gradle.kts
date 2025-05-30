plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.preferences"
}

dependencies {
    implementation(projects.coreNetwork)

    implementation(libs.retrofit.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}