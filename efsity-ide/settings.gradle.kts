rootProject.name = "fct"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":dashboard")
include(":device-manager")
include(":adb")
include(":logcat")
include(":database")
include(":common")
include(":package-manager")
include(":structure-map")
include(":editor")
include(":aurora")
include(":json-tree")
include(":file-manager")
include(":logger")
include(":server-config")
include(":settings")
include(":api-client")
include(":engine")
include(":fhirman")
include(":device-database")
include(":text-viewer")
include(":data-table")
include(":shell")
include(":rules")
include(":workflow")
include(":base64")
include(":insights")
include(":cql")
