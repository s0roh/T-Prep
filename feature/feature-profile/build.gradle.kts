plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.profile"
}

dependencies {
    implementation(project(":data-history"))
    implementation(project(":data-profile"))
    implementation(project(":data-decks"))
    implementation(project(":core-preferences"))
    implementation(project(":core-common"))
    implementation(project(":core-database"))

    implementation(libs.android.image.cropper)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}