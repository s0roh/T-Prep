plugins {
    id("android-app-convention")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.tprep.app"
    defaultConfig {
        applicationId = "com.example.tprep"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(projects.featureDecks)
    implementation(projects.featureAuth)
    implementation(projects.featureTraining)
    implementation(projects.featureHistory)
    implementation(projects.featureLocalDecks)
    implementation(projects.featureReminder)
    implementation(projects.coreNetwork)
    implementation(projects.coreDatabase)
    implementation(projects.corePreferences)
    implementation(projects.dataDecks)
    implementation(projects.dataHistory)
    implementation(projects.dataTraining)
    implementation(projects.dataLocalDecks)
    implementation(projects.dataReminder)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose dependencies
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.icons)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.logging.interceptor)

    // Hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    implementation(libs.work.runtime.ktx)
}