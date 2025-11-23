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
include(":feature")

include(":core:ui")
include(":core:util")
include(":core:model")
include(":core:network")
include(":core:firebase")
include(":core:resources")

include(":feature:auth")
include(":feature:auth:api")
include(":feature:auth:impl")

include(":feature:books")
include(":feature:books:api")
include(":feature:books:impl")

include(":feature:profile")
include(":feature:profile:api")
include(":feature:profile:impl")

include(":feature:reading")
include(":feature:reading:api")
include(":feature:reading:impl")

include(":feature:upload")
include(":feature:upload:api")
include(":feature:upload:impl")
