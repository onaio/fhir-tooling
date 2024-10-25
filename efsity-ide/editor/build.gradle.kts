plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)

    implementation(Dependencies.gson)
    implementation(project(":common"))
    implementation(project(":file-manager"))
    implementation(project(":device-database"))

}

