import com.getkeepsafe.dexcount.OutputFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("android-app-convention")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dexcount)
}

android {
    namespace = "com.example.tprep.app"
    defaultConfig {
        applicationId = "com.example.tprep"
        versionCode = 2
        versionName = "1.0.1"
    }

    val keystoreProperties = Properties()
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    dexcount {
        format.set(OutputFormat.LIST)
        includeClasses.set(true)
        includeFieldCount.set(true)
        includeTotalMethodCount.set(true)
        runOnEachPackage.set(true)
    }
}

dependencies {
    implementation(projects.featureDecks)
    implementation(projects.featureAuth)
    implementation(projects.featureTraining)
    implementation(projects.featureHistory)
    implementation(projects.featureLocalDecks)
    implementation(projects.featureReminder)
    implementation(projects.featureProfile)
    implementation(projects.coreNetwork)
    implementation(projects.coreDatabase)
    implementation(projects.corePreferences)
    implementation(projects.coreCommon)
    implementation(projects.dataDecks)
    implementation(projects.dataHistory)
    implementation(projects.dataTraining)
    implementation(projects.dataLocalDecks)
    implementation(projects.dataReminder)
    implementation(projects.dataProfile)

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

    implementation(libs.android.image.cropper)

    implementation(libs.core.splashscreen)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}