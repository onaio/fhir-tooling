
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)

    implementation(Dependencies.composeView)
    implementation(Dependencies.ApacheCommon.compress)
    implementation(Dependencies.gson)
    implementation(Dependencies.KotlinX.serializationJson)
    implementation(Dependencies.fileKitCompose)

    implementation(project(":common"))
    implementation(project(":api-client"))
    implementation(project(":file-manager"))

}


