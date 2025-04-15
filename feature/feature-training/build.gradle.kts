plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.training"
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-database"))
    implementation(project(":data-training"))
    implementation(project(":data-decks"))
    implementation(project(":data-local-decks"))

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}