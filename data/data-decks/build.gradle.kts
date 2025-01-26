plugins {
    id("android-hilt-lib-convention")
}

android {
    namespace = "com.example.decks"
}

dependencies {
    implementation(project(":core-network"))
    implementation(project(":core-database"))
    implementation(project(":core-preferences"))
    implementation(project(":core-common"))

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}