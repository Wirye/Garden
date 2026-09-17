package com.example.garden.database.converters

import androidx.room.TypeConverter
import com.example.garden.database.ObjectData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ObjectDataConverter {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    @TypeConverter
    fun fromObjectData(data: ObjectData?): String? {
        if (data == null) return null
        return json.encodeToString(data)
    }

    @TypeConverter
    fun toObjectData(value: String?): ObjectData? {
        if (value.isNullOrEmpty()) return null
        return runCatching {
            json.decodeFromString<ObjectData>(value)
        }.getOrNull()
    }
}