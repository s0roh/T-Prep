pluginManagement {
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
include(":app")
include(":core:network")
include(":core:preferences")
include(":feature:auth")
include(":feature:decks")
include(":data:decks")
include(":core:database")
include(":data:history")
include(":data:training")
include(":core:common")
include(":feature:training")
include(":feature:history")
include(":data:local-decks")
include(":feature:local-decks")



//includeNested("network", "core")
//includeNested("preferences", "core")
//includeNested("database", "core")
//includeNested("common", "core")
//
//includeNested("decks", "data")
//includeNested("history", "data")
//includeNested("training", "data")
//includeNested("local-decks", "data")
//
//includeNested("auth", "feature")
//includeNested("decks", "feature")
//includeNested("training", "feature")
//includeNested("history", "feature")
//includeNested("local-decks", "feature")