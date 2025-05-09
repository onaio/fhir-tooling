plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)
    Dependencies.HapiFhir.getAll().forEach(::implementation)

    implementation(Dependencies.SqlDelight.coroutineExtension)
    implementation(Dependencies.ApacheCommon.compress)
    implementation(Dependencies.KotlinX.serializationJson)
    implementation(Dependencies.json)
    implementation(Dependencies.Squareup.okio)

    implementation(project(":common"))
    implementation(project(":database"))
    implementation(project(":editor"))
    implementation(project(":file-manager"))
    implementation(project(":device-database"))
}

