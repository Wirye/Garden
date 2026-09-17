package com.example.garden.database.converters

import androidx.room.TypeConverter
import com.example.garden.database.LinkData

class LinkDataConverter {

    @TypeConverter
    fun fromLinkData(link: LinkData?): String? {
        if (link == null) return null
        return when (link) {
            is LinkData.Insert -> "insert:${link.targetId}"
            is LinkData.Device -> "device:${link.path}"
            is LinkData.Url -> "url:${link.url}"
            LinkData.Self -> "self"
            LinkData.None -> "none"
        }
    }

    @TypeConverter
    fun toLinkData(value: String?): LinkData? {
        if (value.isNullOrEmpty()) return null
        val parts = value.split(":", limit = 2)
        val type = parts.getOrNull(0)
        val data = parts.getOrNull(1).orEmpty()

        return when (type) {
            "insert" -> LinkData.Insert(data.toLongOrNull() ?: 0L)
            "device" -> LinkData.Device(data)
            "url" -> LinkData.Url(data)
            "self" -> LinkData.Self
            "none" -> LinkData.None
            else -> LinkData.None
        }
    }
}