plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.decks"
}

dependencies {
    implementation(project(":data-decks"))
    implementation(project(":data-local-decks"))
    implementation(project(":core-common"))
    implementation(project(":core-database"))

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
}