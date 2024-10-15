
plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)
    implementation(Dependencies.KotlinX.serializationJson)
}