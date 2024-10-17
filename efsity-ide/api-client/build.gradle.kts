
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.jsonSerialization)
}

dependencies {
    Dependencies.HapiFhir.getAll().forEach(::implementation)
    Dependencies.Ktor.getAll().forEach(::implementation)

    implementation(Dependencies.gson)
    implementation(Dependencies.KotlinX.serializationJson)

    implementation(project(":engine"))
    implementation(project(":logger"))
}

