
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)
    Dependencies.HapiFhir.getAll().forEach(::implementation)

    implementation(Dependencies.composeView)
    implementation(Dependencies.KotlinX.serializationJson)
    implementation(Dependencies.SqlDelight.coroutineExtension)
    implementation(Dependencies.json)

    implementation(project(":common"))
    implementation(project(":device-database"))
    implementation(project(":adb"))
    implementation(project(":database"))
    implementation(project(":text-viewer"))
}