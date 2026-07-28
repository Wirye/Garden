package com.example.garden.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object GenreGson {

    val gson: Gson = GsonBuilder().apply {

        // Сначала регистрируем адаптер для самого интерфейса
        registerTypeAdapter(GridGenreItem::class.java, object :
            JsonSerializer<GridGenreItem>,
            JsonDeserializer<GridGenreItem> {

            override fun serialize(
                src: GridGenreItem,
                typeOfSrc: Type,
                context: JsonSerializationContext
            ): JsonElement {
                val json = JsonObject()
                json.addProperty("type", src::class.simpleName)

                when (src) {
                    is Genre -> {
                        json.addProperty("isEnum", true)
                        json.addProperty("name", src.name)
                    }
                    is MusicGenre -> {
                        json.addProperty("isEnum", true)
                        json.addProperty("name", src.name)
                    }
                    else -> {
                        json.add("data", context.serialize(src))
                    }
                }

                return json
            }

            override fun deserialize(
                json: JsonElement,
                typeOfT: Type,
                context: JsonDeserializationContext
            ): GridGenreItem {
                val obj = json.asJsonObject
                val typeName = obj.get("type").asString

                return if (obj.has("isEnum")) {
                    val name = obj.get("name").asString
                    when (typeName) {
                        "Genre" -> Genre.valueOf(name)
                        "MusicGenre" -> MusicGenre.valueOf(name)
                        else -> throw IllegalArgumentException("Unknown enum: $typeName")
                    }
                } else {
                    val data = obj.get("data")
                    when (typeName) {
                        "GenreSezon" -> context.deserialize(data, GenreSezon::class.java)
                        "GenreYear" -> context.deserialize(data, GenreYear::class.java)
                        "GenreAge" -> context.deserialize(data, GenreAge::class.java)
                        "GenreEpisodes" -> context.deserialize(data, GenreEpisodes::class.java)
                        else -> throw IllegalArgumentException("Unknown type: $typeName")
                    }
                }
            }
        })

        // И отдельно для enum'ов, чтобы Gson не использовал дефолтную сериализацию строкой
        registerTypeAdapter(Genre::class.java, JsonSerializer<Genre> { src, _, _ ->
            JsonObject().apply {
                addProperty("type", "Genre")
                addProperty("isEnum", true)
                addProperty("name", src.name)
            }
        })

        registerTypeAdapter(MusicGenre::class.java, JsonSerializer<MusicGenre> { src, _, _ ->
            JsonObject().apply {
                addProperty("type", "MusicGenre")
                addProperty("isEnum", true)
                addProperty("name", src.name)
            }
        })

    }.create()
}
class Converters {
    @TypeConverter
    fun fromGenreList(value: List<GridGenreItem>?): String? {
        return value?.joinToString("|") { item ->
            when (item) {
                is Genre -> "Genre:${item.name}"
                is MusicGenre -> "MusicGenre:${item.name}"
                is GenreSezon -> "GenreSezon:${item.sezon?.name ?: ""}"
                is GenreYear -> "GenreYear:${item.year ?: ""}"
                is GenreAge -> "GenreAge:${item.age ?: ""}"
                is GenreEpisodes -> "GenreEpisodes:"
                else -> ""
            }
        }
    }

    @TypeConverter
    fun toGenreList(value: String?): List<GridGenreItem>? {
        if (value.isNullOrEmpty()) return emptyList()

        return value.split("|").mapNotNull { str ->
            val parts = str.split(":")
            when (parts[0]) {
                "Genre" -> Genre.valueOf(parts[1])
                "MusicGenre" -> MusicGenre.valueOf(parts[1])
                "GenreSezon" -> GenreSezon(sezon = if (parts[1].isEmpty()) null else YearSezon.valueOf(parts[1]))
                "GenreYear" -> GenreYear(year = parts.getOrNull(1)?.toIntOrNull())
                "GenreAge" -> GenreAge(age = parts.getOrNull(1)?.toIntOrNull())
                "GenreEpisodes" -> GenreEpisodes()
                else -> null
            }
        }
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

    @TypeConverter
    fun fromCarouselType(value: CarouselType): String = value.name
    @TypeConverter
    fun toCarouselType(value: String): CarouselType = CarouselType.valueOf(value)
}