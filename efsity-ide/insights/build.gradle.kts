
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.jsonSerialization)
}

dependencies {

    Dependencies.Compose.getAll().forEach(::implementation)
    implementation(Dependencies.KotlinX.serializationJson)

    implementation(project(":common"))
    implementation(project(":adb"))

}
