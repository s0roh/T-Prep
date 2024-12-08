plugins {
    id("android-feature-lib-convention")
}

android {
    namespace = "com.example.feature.reminder"
}

dependencies {
    implementation(project(":data-reminder"))
    implementation(project(":core-database"))
}