package org.smartregister.fct.json.util

import java.util.UUID

internal val randomUUID: String
    get() = UUID.randomUUID().toString()