/*
 * Copyright 2023-2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object Dependencies {

    object Version {
        const val kotlinXCoroutine = "1.9.0-RC"
        const val kotlinXSerializationJson = "1.7.1"
        const val compose = "1.6.11"
        const val fhirVersion = "6.8.0"
        const val hapiFhirCore = "6.0.22"
        const val koin = "4.0.0-RC1"
        const val sqlDelight = "2.0.2"
        const val fileKitCompose = "0.7.0"
        const val kScriptTool = "1.0.22"
        const val constraintLayoutCompose = "0.4.0"
        const val json = "20240303"
        const val prettyTime = "5.0.9.Final"
        const val apacheCommonCollection = "4.5.0-M2"
        const val apacheCommonCompress = "1.26.2"
        const val apacheCommonIO = "2.16.1"
        const val gson = "2.11.0"
        const val compottie = "2.0.0-beta02"
        const val okio = "3.9.0"
        const val decompose = "3.1.0"
        const val essenty = "2.2.0-alpha04"
        const val composeView = "1.6.11.4"
        const val ktor = "2.3.12"
    }

    const val fileKitCompose = "io.github.vinceglb:filekit-compose:${Version.fileKitCompose}"
    const val kScriptTool = "com.sealwu:kscript-tools:${Version.kScriptTool}"
    const val json = "org.json:json:${Version.json}"
    const val prettyTime = "org.ocpsoft.prettytime:prettytime:${Version.prettyTime}"
    const val gson = "com.google.code.gson:gson:${Version.gson}"
    const val compottie = "io.github.alexzhirkevich:compottie:${Version.compottie}"
    const val composeView = "io.github.ltttttttttttt:ComposeViews:${Version.composeView}"

    object Ktor {
        const val clientCore = "io.ktor:ktor-client-core:${Version.ktor}"
        const val clientCio = "io.ktor:ktor-client-cio:${Version.ktor}"
        const val clientAuth = "io.ktor:ktor-client-auth:${Version.ktor}"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:${Version.ktor}"
        const val serializationKotlinX = "io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}"
        const val serializationGson = "io.ktor:ktor-serialization-gson:${Version.ktor}"

        fun getAll() = listOf(
            clientCore,
            clientCio,
            clientAuth,
            contentNegotiation,
            serializationKotlinX,
            serializationGson
        )
    }

    object Decompose {
        const val core = "com.arkivanov.decompose:decompose:${Version.decompose}"
        const val extCompose = "com.arkivanov.decompose:extensions-compose:${Version.decompose}"
        const val lifecycleCoroutines =
            "com.arkivanov.essenty:lifecycle-coroutines:${Version.essenty}"
        const val stateKeeper = "com.arkivanov.essenty:state-keeper:${Version.essenty}"
        fun getAll() = listOf(core, extCompose, lifecycleCoroutines, stateKeeper)
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Version.koin}"
        const val compose = "io.insert-koin:koin-compose:${Version.koin}"

        fun getAll() = listOf(core, compose)
    }

    object KotlinX {
        const val coroutine =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.kotlinXCoroutine}"
        const val serializationJson =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.kotlinXSerializationJson}"

        fun getAll() = listOf(coroutine, serializationJson)
    }

    object Compose {

        const val runtime = "org.jetbrains.compose.runtime:runtime:${Version.compose}"
        const val foundation = "org.jetbrains.compose.foundation:foundation:${Version.compose}"
        const val material3Desktop =
            "org.jetbrains.compose.material3:material3-desktop:${Version.compose}"
        const val ui = "org.jetbrains.compose.ui:ui:${Version.compose}"
        const val componentResources =
            "org.jetbrains.compose.components:components-resources:${Version.compose}"
        const val componentsUiTollingPreview =
            "org.jetbrains.compose.components:components-ui-tooling-preview:${Version.compose}"
        const val materialIconsExtended =
            "org.jetbrains.compose.material:material-icons-extended:${Version.compose}"

        const val constraintLayoutCompose =
            "tech.annexflow.compose:constraintlayout-compose-multiplatform:${Version.constraintLayoutCompose}"

        fun getAll() = listOf(
            runtime,
            foundation,
            material3Desktop,
            ui,
            componentResources,
            componentsUiTollingPreview,
            materialIconsExtended,
            constraintLayoutCompose
        )
    }

    object HapiFhir {

        const val base = "ca.uhn.hapi.fhir:hapi-fhir-base:${Version.fhirVersion}"
        const val structuresR4 = "ca.uhn.hapi.fhir:hapi-fhir-structures-r4:${Version.fhirVersion}"
        const val client = "ca.uhn.hapi.fhir:hapi-fhir-client:${Version.fhirVersion}"
        const val structuresDstu2 =
            "ca.uhn.hapi.fhir:hapi-fhir-structures-dstu2:${Version.fhirVersion}"
        const val fhirR4 = "ca.uhn.hapi.fhir:org.hl7.fhir.r4:${Version.hapiFhirCore}"
        const val fhirR4B = "ca.uhn.hapi.fhir:org.hl7.fhir.r4b:${Version.hapiFhirCore}"
        const val validation = "ca.uhn.hapi.fhir:hapi-fhir-validation:${Version.fhirVersion}"
        const val fhirCoreUtilsModule =
            "ca.uhn.hapi.fhir:org.hl7.fhir.utilities:${Version.hapiFhirCore}"
        const val guavaCachingModule =
            "ca.uhn.hapi.fhir:hapi-fhir-caching-guava:${Version.fhirVersion}"

        fun getAll() =
            listOf(base, structuresR4, client, structuresDstu2, fhirR4, fhirR4B, validation)
    }

    object SqlDelight {
        const val sqliteDriver = "app.cash.sqldelight:sqlite-driver:${Version.sqlDelight}"
        const val coroutineExtension =
            "app.cash.sqldelight:coroutines-extensions:${Version.sqlDelight}"

        fun getAll() = listOf(sqliteDriver, coroutineExtension)
    }

    object ApacheCommon {

        const val collection =
            "org.apache.commons:commons-collections4:${Version.apacheCommonCollection}"
        const val compress = "org.apache.commons:commons-compress:${Version.apacheCommonCompress}"
        const val io = "commons-io:commons-io:${Version.apacheCommonIO}"

        fun getAll() = listOf(collection, compress, io)
    }

    object Squareup {
        const val okio = "com.squareup.okio:okio:${Version.okio}"
    }
}