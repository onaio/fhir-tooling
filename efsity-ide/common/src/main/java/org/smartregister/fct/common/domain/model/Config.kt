package org.smartregister.fct.common.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Config {
    data object Dashboard : Config()
    data object StructureMap : Config()
    data object Workflow : Config()
    data object CQL : Config()
    data object FileManager : Config()
    data object Fhirman : Config()
    data object DeviceDatabase : Config()
    data object Rules : Config()
}