import com.android.build.gradle.internal.dsl.BaseAppModuleExtension

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

configure<BaseAppModuleExtension>{
    baseAndroidConfig()
    buildFeatures {
        compose = true
        buildConfig = true
    }
}