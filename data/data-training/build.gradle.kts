plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.training"
}

dependencies {
    implementation(project(":core-database"))
    implementation(project(":core-common"))
    implementation(project(":core-preferences"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}