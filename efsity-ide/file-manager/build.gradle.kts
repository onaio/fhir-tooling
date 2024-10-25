plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

dependencies {
    Dependencies.Compose.getAll().forEach(::implementation)

    implementation(Dependencies.SqlDelight.coroutineExtension)
    implementation(Dependencies.Squareup.okio)
    implementation(Dependencies.ApacheCommon.io)
    implementation(Dependencies.composeView)

    implementation(project(":common"))
    implementation(project(":database"))
}