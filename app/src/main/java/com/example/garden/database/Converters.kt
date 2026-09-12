package com.example.garden.database

import androidx.room.TypeConverter

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
    fun toGenreList(value: String?): List<GridGenreItem> {
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
    fun fromCornerType(value: SizeType): String = value.name
    @TypeConverter
    fun toCornerType(value: String): SizeType = SizeType.valueOf(value)

    @TypeConverter
    fun fromCardSize(value: CardSize): String = value.name
    @TypeConverter
    fun toCardSize(value: String): CardSize = CardSize.valueOf(value)

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

    @TypeConverter
    fun fromPageType(value: PageType): String = value.name
    @TypeConverter
    fun toPageType(value: String): PageType = PageType.valueOf(value)

    @TypeConverter
    fun fromLayoutType(value: LayoutType): String = value.name
    @TypeConverter
    fun toLayoutType(value: String): LayoutType = LayoutType.valueOf(value)
}

class ImageDataConverter {
    @TypeConverter
    fun fromImageData(imageData: ImageData?): String? {
        return when (imageData) {
            is ImageData.Resource -> "RES:${imageData.ico.name}"
            is ImageData.Device -> "DEV:${imageData.path}"
            is ImageData.Url -> "URL:${imageData.url}"
            null -> null
        }
    }

    @TypeConverter
    fun toImageData(data: String?): ImageData? {
        if (data == null) return null
        return when {
            data.startsWith("RES:") -> {
                val iconName = data.removePrefix("RES:")
                val icon = runCatching { SavedIcons.valueOf(iconName) }
                    .getOrDefault(SavedIcons.CLOSE_ICO)
                ImageData.Resource(icon)
            }
            data.startsWith("DEV:") -> {
                val path = data.removePrefix("DEV:")
                ImageData.Device(path)
            }
            data.startsWith("URL:") -> {
                val url = data.removePrefix("URL:")
                ImageData.Url(url)
            }
            else -> null
        }
    }
}