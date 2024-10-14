package org.smartregister.fct.fm.util

import fct.file_manager.generated.resources.Res
import fct.file_manager.generated.resources.empty_file
import fct.file_manager.generated.resources.folder
import fct.file_manager.generated.resources.hidden_file
import fct.file_manager.generated.resources.hidden_folder
import fct.file_manager.generated.resources.html_file
import fct.file_manager.generated.resources.image_file
import fct.file_manager.generated.resources.json_file
import fct.file_manager.generated.resources.map_file
import fct.file_manager.generated.resources.pdf_file
import fct.file_manager.generated.resources.text_file
import fct.file_manager.generated.resources.unknown_file
import fct.file_manager.generated.resources.zip_file
import okio.Path
import org.jetbrains.compose.resources.DrawableResource


internal fun Path.getFileTypeImage() : DrawableResource {

    return when {
        toFile().isDirectory && !toFile().isHidden -> Res.drawable.folder
        toFile().isDirectory && toFile().isHidden -> Res.drawable.hidden_folder
        toFile().isFile && toFile().isHidden -> Res.drawable.hidden_file
        toFile().extension.isNotEmpty() -> when(toFile().extension){
            "json" -> Res.drawable.json_file
            "map" -> Res.drawable.map_file
            "txt" -> Res.drawable.text_file
            "pdf" -> Res.drawable.pdf_file
            "html", "htm" -> Res.drawable.html_file
            "zip", "7zip", "rar" -> Res.drawable.zip_file
            "jpg", "jpeg", "png", "gif", "bmp", "svg" -> Res.drawable.image_file
            else -> Res.drawable.unknown_file
        }
        toFile().isFile && toFile().length() == 0L -> Res.drawable.empty_file
        else -> Res.drawable.unknown_file
    }
}
