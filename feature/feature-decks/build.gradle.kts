plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.decks"
}

dependencies {
    implementation(project(":data-decks"))
    implementation(project(":data-history"))
    implementation(project(":data-training"))
    implementation(project(":data-local-decks"))
    implementation(project(":core-common"))
    implementation(project(":core-database"))
    implementation(project(":core-preferences"))

    implementation (libs.compose.charts)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
}