
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    implementation(Dependencies.json)
    implementation(Dependencies.KotlinX.coroutine)
    implementation(Dependencies.KotlinX.serializationJson)
    implementation(project(":common"))
    implementation(project(":shell"))
}