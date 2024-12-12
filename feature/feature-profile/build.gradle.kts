plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.profile"
}

dependencies {
    implementation(project(":core-preferences"))
}