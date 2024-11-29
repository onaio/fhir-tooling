
plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)
    implementation(project(":common"))

    implementation(project(":adb"))
}