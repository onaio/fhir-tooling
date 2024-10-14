package org.smartregister.fct.common.util

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Cursor


private val clazz = Class.forName("androidx.compose.ui.input.pointer.AwtCursor").getConstructor(Cursor::class.java)
val windowWidthResizePointer = clazz.newInstance(Cursor(Cursor.W_RESIZE_CURSOR)) as PointerIcon
val windowHeightResizePointer = clazz.newInstance(Cursor(Cursor.N_RESIZE_CURSOR)) as PointerIcon