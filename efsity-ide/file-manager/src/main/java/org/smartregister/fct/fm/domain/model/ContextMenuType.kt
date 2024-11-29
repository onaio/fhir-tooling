package org.smartregister.fct.fm.domain.model

internal sealed class ContextMenuType(val label: String) {
    data object CopyToInternal : ContextMenuType("Copy To Internal")
    data object Upload : ContextMenuType("Upload")
    data object Delete : ContextMenuType("Delete")

    data object SelectFile : ContextMenuType("Select")
}