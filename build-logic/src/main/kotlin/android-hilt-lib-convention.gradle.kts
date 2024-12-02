import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}
android {
    baseModuleConfig()
}
dependencies {
    add("implementation", libs.findLibrary("dagger-hilt-android").get())
    add("ksp", libs.findLibrary("dagger-hilt-compiler").get())
}