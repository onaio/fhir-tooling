package org.smartregister.fct.fm.domain.model

internal sealed class Applicable {
    data class File(val extension: List<String> = listOf()) : Applicable()
    data object Folder : Applicable()
    data object Both : Applicable()
}