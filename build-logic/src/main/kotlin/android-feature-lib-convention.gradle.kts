import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    baseModuleConfig()
    buildFeatures {
        compose = true
    }
}

dependencies {
    add("implementation", libs.findLibrary("androidx-core-ktx").get())
    add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
    add("implementation", libs.findLibrary("androidx-activity-compose").get())
    add("implementation", platform(libs.findLibrary("androidx-compose-bom").get()))
    add("implementation", libs.findLibrary("androidx-ui").get())
    add("implementation", libs.findLibrary("androidx-ui-graphics").get())
    add("implementation", libs.findLibrary("androidx-ui-tooling").get())
    add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
    add("implementation", libs.findLibrary("androidx-material3").get())
    add("implementation", libs.findLibrary("icons").get())
    add("implementation", libs.findLibrary("dagger-hilt-android").get())
    add("ksp", libs.findLibrary("dagger-hilt-compiler").get())
    add("implementation", libs.findLibrary("hilt-navigation-compose").get())
    add("testImplementation", libs.findLibrary("junit").get())
}