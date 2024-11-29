
plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)
    Dependencies.HapiFhir.getAll().forEach(::implementation)


    implementation(Dependencies.json)
    implementation(Dependencies.SqlDelight.coroutineExtension)


    implementation(project(":database"))
    implementation(project(":api-client"))

    api(project(":engine"))
    api(project(":logger"))
    api(project(":aurora"))
    api(project(":json-tree"))
}