
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    Dependencies.Koin.getAll().forEach(::api)
    Dependencies.Decompose.getAll().forEach(::api)
    Dependencies.HapiFhir.getAll().forEach(::implementation)

    api(Dependencies.KotlinX.serializationJson)
    api(Dependencies.KotlinX.coroutine)
    implementation(Dependencies.SqlDelight.coroutineExtension)
    implementation(Dependencies.prettyTime)
    implementation(Dependencies.gson)

    implementation(project(":database"))
    implementation(project(":logger"))
}

