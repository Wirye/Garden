package com.example.garden.database

import androidx.core.R
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CollectionType {
    FastChoiceMusic,
}
enum class CarouselType {
    Anime, Music, Manga, Playlist, AnimeNManga, PlaylistNMusic
}
enum class SizeType {
    SMALL, MEDIUM, LARGE, XLARGE
}
enum class ImageSource {
    SELF,      // Встроенное изображение R.drawable
    DEVICE,     // Изображение на устройстве по пути
    URL
}
interface GridGenreItem {
    val displayNameId: Int
    val colorHex: String
}
enum class Genre : GridGenreItem {
    Drama {
        override val displayNameId: Int = com.example.garden.R.string.Drama
        override val colorHex: String = "#37619F"
    },
    Comedy {
        override val displayNameId: Int = com.example.garden.R.string.Comedy
        override val colorHex: String = "#FFD600"
    },
    Romance {
        override val displayNameId: Int = com.example.garden.R.string.Romance
        override val colorHex: String = "#FF85A2"
    },
    EverydayLife {
        override val displayNameId: Int = com.example.garden.R.string.EverydayLife
        override val colorHex: String = "#A0E4B0"
    },
    School {
        override val displayNameId: Int = com.example.garden.R.string.School
        override val colorHex: String = "#A0E4B0"
    },
    Psychological {
        override val displayNameId: Int = com.example.garden.R.string.Psychological
        override val colorHex: String = "#9D5CFF"
    },
    Shonen {
        override val displayNameId: Int = com.example.garden.R.string.Shonen
        override val colorHex: String = "#FF9100"
    },
    ActionMovie {
        override val displayNameId: Int = com.example.garden.R.string.ActionMovie
        override val colorHex: String = "#FF4B4B"
    },
    MartialArts {
        override val displayNameId: Int = com.example.garden.R.string.MartialArts
        override val colorHex: String = "#FF4B4B"
    },
    Action {
        override val displayNameId: Int = com.example.garden.R.string.Action
        override val colorHex: String = "#FF4B4B"
    },
    Adventures {
        override val displayNameId: Int = com.example.garden.R.string.Adventures
        override val colorHex: String = "#4CAF50"
    },
    Shoujo {
        override val displayNameId: Int = com.example.garden.R.string.Shoujo
        override val colorHex: String = "#F48FB1"
    },
    Fantasy {
        override val displayNameId: Int = com.example.garden.R.string.Fantasy
        override val colorHex: String = "#1FA2FF"
    },
    Isekai {
        override val displayNameId: Int = com.example.garden.R.string.Isekai
        override val colorHex: String = "#00E5FF"
    },
    ScienceFiction {
        override val displayNameId: Int = com.example.garden.R.string.ScienceFiction
        override val colorHex: String = "#2979FF"
    },
    Cyberpunk {
        override val displayNameId: Int = com.example.garden.R.string.Cyberpunk
        override val colorHex: String = "#2979FF"
    },
    Fantastic {
        override val displayNameId: Int = com.example.garden.R.string.Fantastic
        override val colorHex: String = "#2979FF"
    },
    Supernatural {
        override val displayNameId: Int = com.example.garden.R.string.Supernatural
        override val colorHex: String = "#2979FF"
    },
    PostApocalypse {
        override val displayNameId: Int = com.example.garden.R.string.PostApocalypse
        override val colorHex: String = "#8D6E63"
    },
    Detective {
        override val displayNameId: Int = com.example.garden.R.string.Detective
        override val colorHex: String = "#BDBDBD"
    },
    Thriller {
        override val displayNameId: Int = com.example.garden.R.string.Thriller
        override val colorHex: String = "#D32F2F"
    },
    Horrors {
        override val displayNameId: Int = com.example.garden.R.string.Horrors
        override val colorHex: String = "#D32F2F"
    },
    Mysticism {
        override val displayNameId: Int = com.example.garden.R.string.Mysticism
        override val colorHex: String = "#7E57C2"
    },
    Etty {
        override val displayNameId: Int = com.example.garden.R.string.Etty
        override val colorHex: String = "#F06292"
    },
    Harem {
        override val displayNameId: Int = com.example.garden.R.string.Harem
        override val colorHex: String = "#FFD54F"
    },
}
enum class YearSezon {
    WINTER, SUMMER, SPRING, AUTUMN
}

data class GenreSezon (
    override val displayNameId: Int = com.example.garden.R.string.Sezon,
    override val colorHex: String = "#BFDFDFDF",
    val sezon: YearSezon? = null,
)   : GridGenreItem

data class GenreYear (
    override val displayNameId: Int = com.example.garden.R.string.Year,
    override val colorHex: String = "#BFDFDFDF",
    val year: Int? = null,
) : GridGenreItem

data class GenreAge (
    override val displayNameId: Int = com.example.garden.R.string.Age,
    override val colorHex: String = "#BFDFDFDF",
    val age: Int? = null,
) : GridGenreItem

data class GenreEpisodes (
    override val displayNameId: Int = com.example.garden.R.string.EpisodesAnount,
    override val colorHex: String = "#BFDFDFDF",
) : GridGenreItem

enum class MusicGenre : GridGenreItem {
    Rock {
        override val displayNameId = com.example.garden.R.string.Rock
        override val colorHex: String = "#E53935"
    },
    Jazz {
        override val displayNameId = com.example.garden.R.string.Jazz
        override val colorHex: String = "#FFC107"
    },
    LoFi {
        override val displayNameId = com.example.garden.R.string.LoFi
        override val colorHex: String = "#B39DDB"
    },
    Pop {
        override val displayNameId = com.example.garden.R.string.Pop
        override val colorHex: String = "#FF4081"
    },
    Classical {
        override val displayNameId = com.example.garden.R.string.Classical
        override val colorHex: String = "#F5F5DC"
    },
    Metal {
        override val displayNameId = com.example.garden.R.string.Metal
        override val colorHex: String = "#212121"
    },
    Electronic {
        override val displayNameId = com.example.garden.R.string.Electronic
        override val colorHex: String = "#00E5FF"
    },
    HipHop {
        override val displayNameId = com.example.garden.R.string.HipHop
        override val colorHex: String = "#FB8C00"
    },
    Country {
        override val displayNameId = com.example.garden.R.string.Country
        override val colorHex: String = "#8D6E63"
    },
    Ambient {
        override val displayNameId = com.example.garden.R.string.Ambient
        override val colorHex: String = "#1A237E"
    }
}
enum class LinkType {
    SELF,      // Данные из этого же элемента
    INSERT,    // Данные из другого элемента
    CONTENT
}
enum class ElementType {
    Anime,
    Manga,
    Music,
    Playlist,
    Carousel,
    Episode,
}

data class ImageData(
    val source: ImageSource,  // SELF или DEVICE (self - брать данные отсюда, device - с устройства)
    val value: String         // для SELF - id элемента R.drawable, для DEVICE - путь к файлу на диске (например: "C:/загрузки/1.png")
)
data class LinkData(
    val type: LinkType,       // SELF, INSERT, CONTENT || self - брать данные отсюда, insert - брать данные из другого элемента, content - сразу воспроизвести
    val targetId: Long?,       // ID элемента в БД, куда мы идём (нужно, чтобы взять данные из него) (используется для insert)
    val contentPath: String?   // Путь (содержит путь к нужному файлу) (используется для content)
)
@Entity(tableName = "objectData", indices = [androidx.room.Index(value = ["parentId", "position", "lnk_targetId"])])
data class ObjectData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var parentId: Long? = null,

    // Навигация
    var page: Int,              // 0 - home ; 1 - anime ; 2 - manga ; 3 - music ; 4 - download
    var position: Int,          // Позиция на странице/в родителе

    // Имя
    var name: String? = null,   // Название (для карусели или карточки)
    var showAlreadyWatchedLine: Boolean = true,
    var showIco: Boolean = false,  // Это для каруселей, чтобы показывать рядом с названием карусели ник и аву пользователя
    var childsShowName: Boolean = false, // Показывать ли название
    var childsNamePosition: Int? = 0, // 1 - Внутри карточки 0 - снаружи
    var childsShowAlreadyWatchedLine: Boolean = true,
    var childsShowAuthor: Boolean = false,

    // Изображение (оно же превью)
    @Embedded(prefix = "img_")
    var image: ImageData? = null,

    // Доп. инфа
    var description: String? = null,
    var author: String? = null,
    var type: String? = null,  // Аниме, манга, музыка и т.д
    var alreadyWatched: Long, // Минуты и секунды до куда досмотрел пользователь
    var length: Long,  // Минуты и секунды всей длинны
    var carouselType: CarouselType? = null, // Тип карусели, нужен для того, чтобы знать, что туда можно класть (какие карточки добавлять)
    var carouselCollectionType: CollectionType? = null, // Отвечает за подборки карточек

    // Размеры (для карточек)
    var width: Int? = null,
    var height: Int? = null,
    var childsCornerRadius: SizeType? = null,
    var childsBaseWidth: Int? = null,
    var childsBaseHeight: Int? = null,

    var layoutType: Int? = null,  // 0 - сетка (т.е constraint layout с расположенными в виде сетки view вместо recycler view, 1 - с recycler view.
    // Распостраняется и на карточки (0 - карточка - это сетка из карточек (у такой карточки должны быть childs, именно они выступают в роли карточек в сетке, если их нету карточка считается обычной), 1 - обычная карточка)
    var dovodchik: Boolean = false,
    var showDovodchikDots: Boolean = false,

    // Для layout type 0
    var objectsInOneLine: Int? = null,
    var maxLines: Int? = null,
    var adaptiveGridSize: Boolean = false,
    var maxObjectsInOneLineForAdaptiveSize: Int? = null,
    var maxLinesForAdaptiveSize: Int? = null,

    // Это для recycler view параметр (в основном нужно чтобы для удобства отодвинуть view от начала экрана)
    var paddingHorizontal: Int? = null,  // Используется, как marginStart у 1 карточки в recycler и как marginStart/End у constraint layout родителя в grid
    var paddingVertical: Int? = null,  // Используется, как marinTop в каруселей
    var marginBetweenElementsHorizontal: Int? = null,  // Используется, как marginStart у всех карточек, кроме 1 и также, как marginEnd у последней в recycler и как marginStart у всех карточек кроме 1 в grid
    var marginBetweenElementsVertical: Int? = null,  // Используется только в grid, как marginTop у 1 карточки линии, кроме 1 линии

    // Ссылка (куда ведет элемент)
    @Embedded(prefix = "lnk_")
    var link: LinkData? = null,

    // Тип элемента (для удобства фильтрации и только для неё, про макет с.м link -> template)
    var elementType: ElementType,
    var genre: List<GridGenreItem>? = null,
)