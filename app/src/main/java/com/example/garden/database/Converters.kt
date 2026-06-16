package com.example.garden.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromGenreList(value: List<Genre>?): String? = Gson().toJson(value)

    @TypeConverter
    fun toGenreList(value: String?): List<Genre>? {
        val listType = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromImageSource(value: ImageSource): String = value.name
    @TypeConverter
    fun toImageSource(value: String): ImageSource = ImageSource.valueOf(value)

    @TypeConverter
    fun fromCornerType(value: SizeType): String = value.name
    @TypeConverter
    fun toCornerType(value: String): SizeType = SizeType.valueOf(value)

    @TypeConverter
    fun fromLinkType(value: LinkType): String = value.name
    @TypeConverter
    fun toLinkType(value: String): LinkType = LinkType.valueOf(value)

    @TypeConverter
    fun fromElementType(value: ElementType): String = value.name
    @TypeConverter
    fun toElementType(value: String): ElementType = ElementType.valueOf(value)
}