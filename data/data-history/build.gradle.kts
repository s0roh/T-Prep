plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.history"
}

dependencies {
    implementation(project(":core-database"))
    implementation(project(":core-network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}