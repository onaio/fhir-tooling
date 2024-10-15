
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)
    implementation(Dependencies.SqlDelight.coroutineExtension)
    implementation(project(":common"))

    implementation(project(":database"))
    implementation(project(":adb"))
}

