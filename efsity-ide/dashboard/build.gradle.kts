
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

dependencies {

    Dependencies.Compose.getAll().forEach(::implementation)

    implementation(project(":common"))
    implementation(project(":adb"))
    implementation(project(":insights"))

}
