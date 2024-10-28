package org.smartregister.fct.engine.util

object Platform {

    fun getPlatform(): PlatformType {
        val os = System.getProperty("os.name").lowercase()

        return if (os.startsWith("mac os x")) {
            PlatformType.Mac
        } else if (os.startsWith("windows")) {
            PlatformType.Windows
        } else {
            PlatformType.Unix
        }
    }
}

enum class PlatformType {
    Unix, Windows, Mac
}