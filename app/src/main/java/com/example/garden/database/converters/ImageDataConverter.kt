package com.example.garden.database.converters

import androidx.room.TypeConverter
import com.example.garden.database.ImageData
import com.example.garden.database.SavedIcons

class ImageDataConverter {

    @TypeConverter
    fun fromImageData(image: ImageData?): String? {
        if (image == null) return null
        return when (image) {
            is ImageData.Resource -> "resource:${image.ico.name}"
            is ImageData.Device -> "device:${image.path}"
            is ImageData.Url -> "url:${image.url}"
        }
    }

    @TypeConverter
    fun toImageData(value: String?): ImageData? {
        if (value.isNullOrEmpty()) return null
        val parts = value.split(":", limit = 2)
        val type = parts.getOrNull(0)
        val data = parts.getOrNull(1).orEmpty()

        return when (type) {
            "resource" -> {
                val icon = runCatching { SavedIcons.valueOf(data) }.getOrNull()
                    ?: SavedIcons.CLOSE_ICO
                ImageData.Resource(icon)
            }
            "device" -> ImageData.Device(data)
            "url" -> ImageData.Url(data)
            else -> ImageData.Url("")
        }
    }
}
