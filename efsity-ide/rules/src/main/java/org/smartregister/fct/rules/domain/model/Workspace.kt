package org.smartregister.fct.rules.domain.model

internal data class Workspace(
    val id: String,
    val name: String,
    val dataSources: List<Widget<DataSource>>,
    val rules: List<Widget<Rule>>
)