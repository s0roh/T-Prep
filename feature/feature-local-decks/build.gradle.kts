plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.localdecks"
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":data-local-decks"))

    implementation(libs.work.runtime.ktx)
}