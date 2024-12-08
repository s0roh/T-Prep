plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.training"
}

dependencies {
    implementation(project(":core-common"))
    implementation(project(":core-database"))
    implementation(project(":data-training"))
    implementation(project(":data-decks"))
    implementation(project(":data-local-decks"))
}