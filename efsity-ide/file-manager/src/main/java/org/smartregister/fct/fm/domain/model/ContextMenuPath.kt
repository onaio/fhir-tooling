package org.smartregister.fct.fm.domain.model

import okio.Path

internal data class ContextMenuPath(
    val menu: ContextMenu,
    val path: Path
)