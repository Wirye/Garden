package com.example.garden.database

import android.os.Parcelable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.garden.R
import com.example.garden.ui.components.icons.CloseIco
import kotlinx.parcelize.Parcelize

interface CollectionTypeInterface : Parcelable {
    val displayNameId: Int
    val displayIco: ImageData
    val overrideEnabled: Boolean
    val isAddCardEnable: Boolean
    val forCarouselType: List<CarouselType>?
    val overrideNameId: Int
    val overrideChildsCornerRadius: SizeType?
    val overrideChildsShowName: Boolean
    val overrideChildsNamePosition: Int
    val overrideChildsShowAlreadyWatchedLine: Boolean
    val overrideLayoutType: LayoutType
    val overrideObjectsInOneLine: Int?
    val overrideMaxLines: Int?
    val overrideDovodchik: Boolean
    val overrideShowDovodchikDots: Boolean
    val overrideAdaptiveGridSize: Boolean
    val overrideMaxObjectsInOneLineForAdaptiveSize: Int?
    val overrideMaxLinesForAdaptiveSize: Int?
    val overrideChildsSize: CardSize
    val overrideChildsShowAuthor: Boolean
    val overrideShowIco: Boolean
    val overrideIco: ImageData?
}

@Parcelize
enum class CollectionType : CollectionTypeInterface {
    None {
        override val displayNameId: Int = R.string.absent
        override val displayIco: ImageData = ImageData.Resource(SavedIcons.CLOSE_ICO)
        override val overrideEnabled: Boolean = false
        override val isAddCardEnable: Boolean = true
        override val forCarouselType: List<CarouselType>? = null
        override val overrideNameId: Int = R.string.spacer
        override val overrideChildsCornerRadius: SizeType? = null
        override val overrideChildsShowName: Boolean = false
        override val overrideChildsNamePosition: Int = 0
        override val overrideChildsShowAlreadyWatchedLine: Boolean = true
        override val overrideLayoutType: LayoutType = LayoutType.DEFAULT
        override val overrideObjectsInOneLine: Int? = null
        override val overrideMaxLines: Int? = null
        override val overrideDovodchik: Boolean = false
        override val overrideShowDovodchikDots: Boolean = false
        override val overrideAdaptiveGridSize: Boolean = false
        override val overrideMaxObjectsInOneLineForAdaptiveSize: Int? = null
        override val overrideMaxLinesForAdaptiveSize: Int? = null
        override val overrideChildsSize: CardSize = CardSize.MEDIUM
        override val overrideChildsShowAuthor: Boolean = false
        override val overrideShowIco: Boolean = false
        override val overrideIco: ImageData? = null
    },
    FastChoiceMusic {
        override val displayNameId: Int = R.string.fastChoiceYouTubeMusic
        override val displayIco: ImageData = ImageData.Url("https://www.google.com/s2/favicons?domain=music.youtube.com&sz=128")
        override val overrideEnabled: Boolean = true
        override val isAddCardEnable: Boolean = true
        override val forCarouselType: List<CarouselType> = listOf(CarouselType.Music, CarouselType.PlaylistNMusic)
        override val overrideNameId: Int = R.string.fastChoiceYouTubeMusic
        override val overrideChildsCornerRadius: SizeType = SizeType.SMALL
        override val overrideChildsShowName: Boolean = true
        override val overrideChildsNamePosition: Int = 1
        override val overrideChildsShowAlreadyWatchedLine: Boolean = false
        override val overrideLayoutType: LayoutType = LayoutType.CAROUSEL_FROM_GRID
        override val overrideObjectsInOneLine: Int = 3
        override val overrideMaxLines: Int = 3
        override val overrideDovodchik: Boolean = true
        override val overrideShowDovodchikDots: Boolean = true
        override val overrideAdaptiveGridSize: Boolean = true
        override val overrideMaxObjectsInOneLineForAdaptiveSize: Int = 4
        override val overrideMaxLinesForAdaptiveSize: Int = 2
        override val overrideChildsSize: CardSize = CardSize.SMALL
        override val overrideChildsShowAuthor: Boolean = false
        override val overrideShowIco: Boolean = true
        override val overrideIco: ImageData = ImageData.Url("https://www.google.com/s2/favicons?domain=music.youtube.com&sz=256")
    }
}

@Parcelize
enum class PageType : Parcelable {
    Home, Anime, Manga, Music, Playlist, Download
}

@Parcelize
enum class LayoutType : Parcelable {
    DEFAULT, CAROUSEL_GRID, CAROUSEL_FROM_GRID, CARD_GRID, CAROUSEL_FROM_FLAT_GRID, CARD_FLAT_GRID,
    FLAT_GRID_ITEM
}

interface CarouselTypeInterface : Parcelable {
    val displayNameId: Int
}

@Parcelize
enum class CarouselType : CarouselTypeInterface {
    Anime {
        override val displayNameId: Int = R.string.Anime
    },
    Manga {
        override val displayNameId: Int = R.string.Manga
    },
    Music {
        override val displayNameId: Int = R.string.Music
    },
    Playlist {
        override val displayNameId: Int = R.string.Playlist
    },
    AnimeNManga {
        override val displayNameId: Int = R.string.AnimeNManga
    },
    PlaylistNMusic {
        override val displayNameId: Int = R.string.PlaylistNMusic
    }
}

@Parcelize
enum class SizeType : Parcelable {
    ESMALL, SMALL, MEDIUM, LARGE, XLARGE
}

interface GridGenreItem : Parcelable {
    val displayNameId: Int
    val colorHex: String
}

@Parcelize
enum class Genre : GridGenreItem {
    Drama {
        override val displayNameId: Int = R.string.Drama
        override val colorHex: String = "#37619F"
    },
    Comedy {
        override val displayNameId: Int = R.string.Comedy
        override val colorHex: String = "#FFD600"
    },
    Romance {
        override val displayNameId: Int = R.string.Romance
        override val colorHex: String = "#FF85A2"
    },
    EverydayLife {
        override val displayNameId: Int = R.string.EverydayLife
        override val colorHex: String = "#A0E4B0"
    },
    School {
        override val displayNameId: Int = R.string.School
        override val colorHex: String = "#A0E4B0"
    },
    Psychological {
        override val displayNameId: Int = R.string.Psychological
        override val colorHex: String = "#9D5CFF"
    },
    Shonen {
        override val displayNameId: Int = R.string.Shonen
        override val colorHex: String = "#FF9100"
    },
    ActionMovie {
        override val displayNameId: Int = R.string.ActionMovie
        override val colorHex: String = "#FF4B4B"
    },
    MartialArts {
        override val displayNameId: Int = R.string.MartialArts
        override val colorHex: String = "#FF4B4B"
    },
    Action {
        override val displayNameId: Int = R.string.Action
        override val colorHex: String = "#FF4B4B"
    },
    Adventures {
        override val displayNameId: Int = R.string.Adventures
        override val colorHex: String = "#4CAF50"
    },
    Shoujo {
        override val displayNameId: Int = R.string.Shoujo
        override val colorHex: String = "#F48FB1"
    },
    Fantasy {
        override val displayNameId: Int = R.string.Fantasy
        override val colorHex: String = "#1FA2FF"
    },
    Isekai {
        override val displayNameId: Int = R.string.Isekai
        override val colorHex: String = "#00E5FF"
    },
    ScienceFiction {
        override val displayNameId: Int = R.string.ScienceFiction
        override val colorHex: String = "#2979FF"
    },
    Cyberpunk {
        override val displayNameId: Int = R.string.Cyberpunk
        override val colorHex: String = "#2979FF"
    },
    Fantastic {
        override val displayNameId: Int = R.string.Fantastic
        override val colorHex: String = "#2979FF"
    },
    Supernatural {
        override val displayNameId: Int = R.string.Supernatural
        override val colorHex: String = "#2979FF"
    },
    PostApocalypse {
        override val displayNameId: Int = R.string.PostApocalypse
        override val colorHex: String = "#8D6E63"
    },
    Detective {
        override val displayNameId: Int = R.string.Detective
        override val colorHex: String = "#BDBDBD"
    },
    Thriller {
        override val displayNameId: Int = R.string.Thriller
        override val colorHex: String = "#D32F2F"
    },
    Horrors {
        override val displayNameId: Int = R.string.Horrors
        override val colorHex: String = "#D32F2F"
    },
    Mysticism {
        override val displayNameId: Int = R.string.Mysticism
        override val colorHex: String = "#7E57C2"
    },
    Etty {
        override val displayNameId: Int = R.string.Etty
        override val colorHex: String = "#F06292"
    },
    Harem {
        override val displayNameId: Int = R.string.Harem
        override val colorHex: String = "#FFD54F"
    },
}

interface YearSezonInterface : Parcelable {
    val displayNameId: Int
}

@Parcelize
enum class YearSezon : YearSezonInterface {
    WINTER {
        override val displayNameId = R.string.Winter
    },
    SUMMER {
        override val displayNameId = R.string.Summer
    },
    SPRING {
        override val displayNameId = R.string.Spring
    },
    AUTUMN {
        override val displayNameId = R.string.Autumn
    }
}

@Parcelize
data class GenreSezon (
    override val displayNameId: Int = R.string.Sezon,
    override val colorHex: String = "#BFDFDFDF",
    val sezon: YearSezon? = null,
)   : GridGenreItem

@Parcelize
data class GenreYear (
    override val displayNameId: Int = R.string.Year,
    override val colorHex: String = "#BFDFDFDF",
    var year: Int? = null,
) : GridGenreItem

@Parcelize
data class GenreAge (
    override val displayNameId: Int = R.string.Age,
    override val colorHex: String = "#BFDFDFDF",
    val age: Int? = null,
) : GridGenreItem

@Parcelize
data class GenreEpisodes (
    override val displayNameId: Int = R.string.EpisodesAnount,
    override val colorHex: String = "#BFDFDFDF",
) : GridGenreItem

@Parcelize
enum class MusicGenre : GridGenreItem {
    Rock {
        override val displayNameId = R.string.Rock
        override val colorHex: String = "#E53935"
    },
    Jazz {
        override val displayNameId = R.string.Jazz
        override val colorHex: String = "#FFC107"
    },
    LoFi {
        override val displayNameId = R.string.LoFi
        override val colorHex: String = "#B39DDB"
    },
    Pop {
        override val displayNameId = R.string.Pop
        override val colorHex: String = "#FF4081"
    },
    Classical {
        override val displayNameId = R.string.Classical
        override val colorHex: String = "#F5F5DC"
    },
    Metal {
        override val displayNameId = R.string.Metal
        override val colorHex: String = "#212121"
    },
    Electronic {
        override val displayNameId = R.string.Electronic
        override val colorHex: String = "#00E5FF"
    },
    HipHop {
        override val displayNameId = R.string.HipHop
        override val colorHex: String = "#FB8C00"
    },
    Country {
        override val displayNameId = R.string.Country
        override val colorHex: String = "#8D6E63"
    },
    Ambient {
        override val displayNameId = R.string.Ambient
        override val colorHex: String = "#1A237E"
    }
}

@Parcelize
enum class LinkType : Parcelable {
    SELF,      // Данные из этого же элемента
    INSERT,    // Данные из другого элемента
    CONTENT
}

interface ElementTypeInterface : Parcelable {
    val displayNameId: Int
}

@Parcelize
enum class ElementType : ElementTypeInterface {
    AnimeCard {
        override val displayNameId = R.string.Anime
    },
    MangaCard {
        override val displayNameId = R.string.Manga
    },
    MusicCard {
        override val displayNameId = R.string.Music
    },
    PlaylistCard {
        override val displayNameId = R.string.Playlist
    },
    PlaceholderCard {
        override val displayNameId = R.string.Placeholder
    },
    Carousel {
        override val displayNameId = R.string.Carousel
    },
    Episode {
        override val displayNameId = R.string.Episode
    },
    Chapter {
        override val displayNameId = R.string.Chapter
    },
    ChapterPage {
        override val displayNameId = R.string.ChapterPage
    },
    Song {
        override val displayNameId = R.string.Song
    },
    SongVerticalVideo {
        override val displayNameId = R.string.SongVerticalVideo
    },
    SongHorizontalVideo {
        override val displayNameId = R.string.SongHorizontalVideo
    },
}

@Parcelize
enum class CardSize : Parcelable {
    SMALL, MEDIUM, LARGE
}

@Parcelize
enum class SavedIcons : Parcelable {
    CLOSE_ICO;

    val imageVector: ImageVector
        get() = when (this) {
            CLOSE_ICO -> CloseIco
        }
}

sealed interface ImageData : Parcelable {

    @Parcelize
    data class Resource(
        val ico: SavedIcons
    ) : ImageData

    @Parcelize
    data class Device(
        val path: String
    ) : ImageData

    @Parcelize
    data class Url(
        val url: String
    ) : ImageData
}


@Parcelize
data class LinkData(
    val type: LinkType,       // SELF, INSERT, CONTENT || self - брать данные отсюда, insert - брать данные из другого элемента, content - сразу воспроизвести
    val targetId: Long?,       // ID элемента в БД, куда мы идём (нужно, чтобы взять данные из него) (используется для insert)
    val contentPath: String?   // Путь (содержит путь к нужному файлу) (используется для content)
) : Parcelable

@Entity(tableName = "objectData", indices = [androidx.room.Index(value = ["parentId", "position", "lnk_targetId"])])
data class ObjectData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: Long? = null,

    // Навигация
    val page: PageType,
    val position: Int,          // Позиция на странице/в родителе

    // Имя
    val name: String? = null,   // Название (для карусели или карточки)
    val showAlreadyWatchedLine: Boolean = true,
    val showIco: Boolean = false,  // Это для каруселей, чтобы показывать рядом с названием карусели ник и аватарку пользователя
    val childsShowName: Boolean = false, // Показывать ли название
    val childsNamePosition: Int? = 0, // 1 - Внутри карточки 0 - снаружи
    val childsShowAlreadyWatchedLine: Boolean = true,
    val childsShowAuthor: Boolean = false,

    // Изображение (оно же превью)
    val image: ImageData? = null,

    // Доп. инфа
    val description: String? = null,
    val author: String? = null,
    val type: String? = null,  // Аниме, манга, музыка и т.д
    val alreadyWatched: Long, // Минуты и секунды до куда досмотрел пользователь
    val length: Long,  // Минуты и секунды всей длинны
    val carouselType: CarouselType? = null, // Тип карусели, нужен для того, чтобы знать, что туда можно класть (какие карточки добавлять)
    val carouselCollectionType: CollectionType? = null, // Отвечает за подборки карточек

    // Размеры (для карточек)
    val childsCornerRadius: SizeType? = null,
    val childsSize: CardSize? = null,

    val layoutType: LayoutType = LayoutType.DEFAULT,

    val dovodchik: Boolean = false,
    val showDovodchikDots: Boolean = false,

    // Для grid layout type
    val objectsInOneLine: Int? = null,
    val maxLines: Int? = null,
    val adaptiveGridSize: Boolean = false,
    val maxObjectsInOneLineForAdaptiveSize: Int? = null,
    val maxLinesForAdaptiveSize: Int? = null,

    // Ссылка (куда ведет элемент)
    @Embedded(prefix = "lnk_")
    val link: LinkData? = null,

    // Тип элемента (для удобства фильтрации и только для неё, про макет с.м link -> template)
    val elementType: ElementType,
    val genre: List<GridGenreItem>? = null,
)