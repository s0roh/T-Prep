pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

private fun includeNested(lib: String, libRootDir: String) {
    include(":$lib")
    project(":$lib").projectDir = file("$libRootDir/$lib")
}

rootProject.name = "T-Prep"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")

includeNested("core-network", "core")
includeNested("core-preferences", "core")
includeNested("core-database", "core")
includeNested("core-common", "core")

includeNested("data-decks", "data")
includeNested("data-history", "data")
includeNested("data-training", "data")
includeNested("data-local-decks", "data")
includeNested("data-reminder", "data")

includeNested("feature-auth", "feature")
includeNested("feature-decks", "feature")
includeNested("feature-training", "feature")
includeNested("feature-history", "feature")
includeNested("feature-local-decks", "feature")
includeNested("feature-reminder", "feature")