
plugins {
    alias(libs.plugins.jetbrainsKotlinJvm)
    alias(libs.plugins.sqlDelight)
}

dependencies {
    Dependencies.SqlDelight.getAll().forEach(::implementation)
    Dependencies.Koin.getAll().forEach(::implementation)

    implementation(Dependencies.KotlinX.serializationJson)
    implementation(Dependencies.SqlDelight.sqliteDriver)
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("org.smartregister.fct.database")
            srcDirs.setFrom("src/main/sqldelight")
        }
    }
}