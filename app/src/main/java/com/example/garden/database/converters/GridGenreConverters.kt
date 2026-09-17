package com.example.garden.database.converters

import androidx.room.TypeConverter
import com.example.garden.database.GridGenreItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GridGenreConverters {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromGenreList(value: List<GridGenreItem>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toGenreList(value: String): List<GridGenreItem> {
        return json.decodeFromString(value)
    }
}