plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.history"
}

dependencies {
    implementation(project(":data-history"))
    implementation(project(":data-decks"))
    implementation(project(":core-common"))
    implementation(project(":core-database"))
}