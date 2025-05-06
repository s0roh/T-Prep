import com.android.build.gradle.BaseExtension

fun BaseExtension.baseModuleConfig() {
    setCompileSdkVersion(AndroidConst.COMPILE_SKD)
    defaultConfig {
        minSdk = AndroidConst.MIN_SKD
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("consumer-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = AndroidConst.COMPILE_JDK_VERSION
        targetCompatibility = AndroidConst.COMPILE_JDK_VERSION
    }
}

fun BaseExtension.baseAndroidConfig() {
    setCompileSdkVersion(AndroidConst.COMPILE_SKD)
    defaultConfig {
        minSdk = AndroidConst.MIN_SKD
        targetSdk = AndroidConst.TARGET_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = AndroidConst.COMPILE_JDK_VERSION
        targetCompatibility = AndroidConst.COMPILE_JDK_VERSION
    }
}
