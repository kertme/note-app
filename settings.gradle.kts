pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

rootProject.name = "NoteApp"
include (":app")
include(":features:home:domain")
include(":features:home:data")
include(":core:datasource:local")
include(":features:home:ui")
include(":features:note_details:domain")
include(":core:di")
include(":features:note_details:data")
include(":core:ui")
include(":core:domain")
include(":features:home:shared")
include(":features:note_details:shared")
include(":features:note_details:ui")