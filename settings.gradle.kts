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

rootProject.name = "Avito"
include(":app")
include(":core")
include(":core:ui")
include(":feature")

include(":feature:auth")
include(":feature:books")
include(":feature:profile")
include(":feature:reading")
include(":feature:upload")
include(":feature:auth:api")
include(":feature:auth:impl")
