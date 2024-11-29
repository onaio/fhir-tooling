package org.smartregister.fct.rules.domain.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import kotlinx.serialization.Serializable


@Serializable
internal data class Rule(
    val id: String,
    val name: String,
    val priority: Int,
    val condition: String,
    val description: String,
    val actions: List<String>,

    @kotlinx.serialization.Transient
    var result: AnnotatedString = buildAnnotatedString {  }
) : java.io.Serializable
