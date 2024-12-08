plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.localdecks"
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":data-local-decks"))
}