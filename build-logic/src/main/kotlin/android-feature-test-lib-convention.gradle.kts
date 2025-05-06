plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    add("testImplementation", libs.findLibrary("junit-jupiter-api").get())
    add("testImplementation", libs.findLibrary("junit-jupiter-params").get())
    add("testRuntimeOnly", libs.findLibrary("junit-jupiter-engine").get())
    add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
    add("testImplementation", libs.findLibrary("mockk").get())
    add("testImplementation", libs.findLibrary("truth").get())
    add("testImplementation", libs.findLibrary("coroutines-test").get())
    add("testImplementation", libs.findLibrary("turbine").get())
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}