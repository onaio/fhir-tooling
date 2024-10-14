package org.smartregister.fct.rules.domain.model

import java.awt.Point

internal data class BoardProperty(
    val width: Int,
    val height: Int,
    val center: Point
)