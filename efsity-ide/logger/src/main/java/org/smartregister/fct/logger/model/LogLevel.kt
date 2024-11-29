package org.smartregister.fct.logger.model

import androidx.compose.ui.graphics.Color

enum class LogLevel(val value: String, val color: Color) {

    VERBOSE("V", Color(0xff77b237)),
    DEBUG("D", Color(0xff5d95fa)),
    INFO("I", Color(0xff77b237)),
    WARNING("W", Color(0xffd59815)),
    ERROR("E", Color(0xfffa5d5d)),
    ASSERT("A", Color(0xff77b237)),
}

