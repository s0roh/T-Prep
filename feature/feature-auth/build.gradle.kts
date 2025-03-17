plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.auth"
}

dependencies {
    implementation(project(":core-network"))
    implementation(project(":core-preferences"))
    implementation(project(":data-local-decks"))

    implementation(libs.retrofit.core)
}