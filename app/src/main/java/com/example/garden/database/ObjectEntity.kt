package com.example.garden.database

import android.os.Parcelable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.garden.R
import com.example.garden.ui.components.icons.CloseIco
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface CollectionTypeInterface : Parcelable {
    val displayNameId: Int
    val displayIco: ImageData
    val overrideEnabled: Boolean
    val isAddCardEnable: Boolean
    val forCarouselType: List<CarouselType>?
    val overrideNameId: Int
    val overrideChildsCornerRadius: SizeType
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
@Serializable
enum class CollectionType : CollectionTypeInterface {
    None {
        override val displayNameId: Int = R.string.absent
        override val displayIco: ImageData = ImageData.Resource(SavedIcons.CLOSE_ICO)
        override val overrideEnabled: Boolean = false
        override val isAddCardEnable: Boolean = true
        override val forCarouselType: List<CarouselType>? = null
        override val overrideNameId: Int = R.string.spacer
        override val overrideChildsCornerRadius: SizeType = SizeType.ESMALL
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
        override val displayIco: ImageData =
            ImageData.Url("https://www.google.com/s2/favicons?domain=music.youtube.com&sz=128")
        override val overrideEnabled: Boolean = true
        override val isAddCardEnable: Boolean = true
        override val forCarouselType: List<CarouselType> =
            listOf(CarouselType.Music, CarouselType.PlaylistNMusic)
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
        override val overrideIco: ImageData =
            ImageData.Url("https://www.google.com/s2/favicons?domain=music.youtube.com&sz=256")
    }
}

@Parcelize
@Serializable
enum class PageType : Parcelable {
    Home, Anime, Manga, Music, Playlist, Download
}

@Parcelize
@Serializable
enum class LayoutType : Parcelable {
    DEFAULT, CAROUSEL_GRID, CAROUSEL_FROM_GRID, CARD_GRID, CAROUSEL_FROM_FLAT_GRID, CARD_FLAT_GRID,
    FLAT_GRID_ITEM
}

interface CarouselTypeInterface : Parcelable {
    val displayNameId: Int
}

@Parcelize
@Serializable
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
@Serializable
enum class SizeType : Parcelable {
    ESMALL, SMALL, MEDIUM, LARGE, XLARGE
}

@Serializable
sealed interface GridGenreItem : Parcelable {
    val displayNameId: Int
    val colorHex: String
}

@Serializable
sealed interface YearSezonInterface : Parcelable {
    val displayNameId: Int
}

@Serializable
sealed interface Genre : GridGenreItem {
    @Parcelize
    @Serializable
    data object Drama : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Drama

        @IgnoredOnParcel
        override val colorHex = "#37619F"
    }

    @Parcelize
    @Serializable
    data object Comedy : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Comedy

        @IgnoredOnParcel
        override val colorHex = "#FFD600"
    }

    @Parcelize
    @Serializable
    data object Romance : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Romance

        @IgnoredOnParcel
        override val colorHex = "#FF85A2"
    }

    @Parcelize
    @Serializable
    data object EverydayLife : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.EverydayLife

        @IgnoredOnParcel
        override val colorHex = "#A0E4B0"
    }

    @Parcelize
    @Serializable
    data object School : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.School

        @IgnoredOnParcel
        override val colorHex = "#A0E4B0"
    }

    @Parcelize
    @Serializable
    data object Psychological : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Psychological

        @IgnoredOnParcel
        override val colorHex = "#9D5CFF"
    }

    @Parcelize
    @Serializable
    data object Shonen : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Shonen

        @IgnoredOnParcel
        override val colorHex = "#FF9100"
    }

    @Parcelize
    @Serializable
    data object ActionMovie : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.ActionMovie

        @IgnoredOnParcel
        override val colorHex = "#FF4B4B"
    }

    @Parcelize
    @Serializable
    data object MartialArts : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.MartialArts

        @IgnoredOnParcel
        override val colorHex = "#FF4B4B"
    }

    @Parcelize
    @Serializable
    data object Action : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Action

        @IgnoredOnParcel
        override val colorHex = "#FF4B4B"
    }

    @Parcelize
    @Serializable
    data object Adventures : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Adventures

        @IgnoredOnParcel
        override val colorHex = "#4CAF50"
    }

    @Parcelize
    @Serializable
    data object Shoujo : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Shoujo

        @IgnoredOnParcel
        override val colorHex = "#F48FB1"
    }

    @Parcelize
    @Serializable
    data object Fantasy : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Fantasy

        @IgnoredOnParcel
        override val colorHex = "#1FA2FF"
    }

    @Parcelize
    @Serializable
    data object Isekai : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Isekai

        @IgnoredOnParcel
        override val colorHex = "#00E5FF"
    }

    @Parcelize
    @Serializable
    data object ScienceFiction : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.ScienceFiction

        @IgnoredOnParcel
        override val colorHex = "#2979FF"
    }

    @Parcelize
    @Serializable
    data object Cyberpunk : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Cyberpunk

        @IgnoredOnParcel
        override val colorHex = "#2979FF"
    }

    @Parcelize
    @Serializable
    data object Fantastic : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Fantastic

        @IgnoredOnParcel
        override val colorHex = "#2979FF"
    }

    @Parcelize
    @Serializable
    data object Supernatural : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Supernatural

        @IgnoredOnParcel
        override val colorHex = "#2979FF"
    }

    @Parcelize
    @Serializable
    data object PostApocalypse : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.PostApocalypse

        @IgnoredOnParcel
        override val colorHex = "#8D6E63"
    }

    @Parcelize
    @Serializable
    data object Detective : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Detective

        @IgnoredOnParcel
        override val colorHex = "#BDBDBD"
    }

    @Parcelize
    @Serializable
    data object Thriller : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Thriller

        @IgnoredOnParcel
        override val colorHex = "#D32F2F"
    }

    @Parcelize
    @Serializable
    data object Horrors : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Horrors

        @IgnoredOnParcel
        override val colorHex = "#D32F2F"
    }

    @Parcelize
    @Serializable
    data object Mysticism : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Mysticism

        @IgnoredOnParcel
        override val colorHex = "#7E57C2"
    }

    @Parcelize
    @Serializable
    data object Etty : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Etty

        @IgnoredOnParcel
        override val colorHex = "#F06292"
    }

    @Parcelize
    @Serializable
    data object Harem : Genre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Harem

        @IgnoredOnParcel
        override val colorHex = "#FFD54F"
    }

    companion object {
        val entries: List<Genre> by lazy {
            listOf(
                Drama, Comedy, Romance, EverydayLife, School, Psychological,
                Shonen, ActionMovie, MartialArts, Action, Adventures, Shoujo,
                Fantasy, Isekai, ScienceFiction, Cyberpunk, Fantastic,
                Supernatural, PostApocalypse, Detective, Thriller, Horrors,
                Mysticism, Etty, Harem
            )
        }
    }
}

@Serializable
sealed interface YearSezon : YearSezonInterface {
    @Parcelize
    @Serializable
    data object WINTER : YearSezon {
        @IgnoredOnParcel
        override val displayNameId = R.string.Winter
    }

    @Parcelize
    @Serializable
    data object SUMMER : YearSezon {
        @IgnoredOnParcel
        override val displayNameId = R.string.Summer
    }

    @Parcelize
    @Serializable
    data object SPRING : YearSezon {
        @IgnoredOnParcel
        override val displayNameId = R.string.Spring
    }

    @Parcelize
    @Serializable
    data object AUTUMN : YearSezon {
        @IgnoredOnParcel
        override val displayNameId = R.string.Autumn
    }

    companion object {
        val entries: List<YearSezon> by lazy {
            listOf(WINTER, SUMMER, SPRING, AUTUMN)
        }
    }
}

@Parcelize
@Serializable
data class GenreSezon(
    override val displayNameId: Int = R.string.Sezon,
    override val colorHex: String = "#BFDFDFDF",
    val sezon: YearSezon? = null,
) : GridGenreItem

@Parcelize
@Serializable
data class GenreYear(
    override val displayNameId: Int = R.string.Year,
    override val colorHex: String = "#BFDFDFDF",
    var year: Int? = null,
) : GridGenreItem

@Parcelize
@Serializable
data class GenreAge(
    override val displayNameId: Int = R.string.Age,
    override val colorHex: String = "#BFDFDFDF",
    val age: Int? = null,
) : GridGenreItem

@Parcelize
@Serializable
data class GenreEpisodes(
    override val displayNameId: Int = R.string.EpisodesAnount,
    override val colorHex: String = "#BFDFDFDF",
) : GridGenreItem

@Serializable
sealed interface MusicGenre : GridGenreItem {
    @Parcelize
    @Serializable
    data object Rock : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Rock

        @IgnoredOnParcel
        override val colorHex = "#E53935"
    }

    @Parcelize
    @Serializable
    data object Jazz : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Jazz

        @IgnoredOnParcel
        override val colorHex = "#FFC107"
    }

    @Parcelize
    @Serializable
    data object LoFi : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.LoFi

        @IgnoredOnParcel
        override val colorHex = "#B39DDB"
    }

    @Parcelize
    @Serializable
    data object Pop : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Pop

        @IgnoredOnParcel
        override val colorHex = "#FF4081"
    }

    @Parcelize
    @Serializable
    data object Classical : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Classical

        @IgnoredOnParcel
        override val colorHex = "#F5F5DC"
    }

    @Parcelize
    @Serializable
    data object Metal : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Metal

        @IgnoredOnParcel
        override val colorHex = "#212121"
    }

    @Parcelize
    @Serializable
    data object Electronic : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Electronic

        @IgnoredOnParcel
        override val colorHex = "#00E5FF"
    }

    @Parcelize
    @Serializable
    data object HipHop : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.HipHop

        @IgnoredOnParcel
        override val colorHex = "#FB8C00"
    }

    @Parcelize
    @Serializable
    data object Country : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Country

        @IgnoredOnParcel
        override val colorHex = "#8D6E63"
    }

    @Parcelize
    @Serializable
    data object Ambient : MusicGenre {
        @IgnoredOnParcel
        override val displayNameId = R.string.Ambient

        @IgnoredOnParcel
        override val colorHex = "#1A237E"
    }

    companion object {
        val entries: List<MusicGenre> = listOf(
            Rock, Jazz, LoFi, Pop, Classical, Metal, Electronic, HipHop, Country, Ambient
        )
    }
}

interface ElementTypeInterface : Parcelable {
    val displayNameId: Int
}

@Parcelize
@Serializable
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
@Serializable
enum class CardSize : Parcelable {
    SMALL, MEDIUM, LARGE
}

@Parcelize
@Serializable
enum class SavedIcons : Parcelable {
    CLOSE_ICO;

    val imageVector: ImageVector
        get() = when (this) {
            CLOSE_ICO -> CloseIco
        }
}

@Serializable
@Parcelize
sealed interface ImageData : Parcelable {

    @Parcelize
    @Serializable
    data class Resource(
        val ico: SavedIcons
    ) : ImageData

    @Parcelize
    @Serializable
    data class Device(
        val path: String
    ) : ImageData

    @Parcelize
    @Serializable
    data class Url(
        val url: String
    ) : ImageData
}

@Serializable
@Parcelize
sealed interface LinkData : Parcelable {

    @Parcelize
    @Serializable
    data class Insert(
        val targetId: Long
    ) : LinkData

    @Parcelize
    @Serializable
    data class Device(
        val path: String
    ) : LinkData

    @Parcelize
    @Serializable
    data class Url(
        val url: String
    ) : LinkData

    @Parcelize
    @Serializable
    object Self : LinkData

    @Parcelize
    @Serializable
    object None : LinkData
}


@Entity(
    tableName = "objectData",
    indices = [
        Index(value = ["parentId", "position", "link"]),
        Index(value = ["elementType"])
    ]
)
data class ObjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: Long? = null,

    val page: PageType,
    val position: Int,

    val elementType: ElementType,

    val link: LinkData,

    val isUserCreated: Boolean,

    val name: String,

    val info: ObjectData
)

@Entity(tableName = "objectData_fts")
@Fts4(contentEntity = ObjectEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
data class ObjectFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowid: Long,
    val name: String
)

@Entity(
    tableName = "episode_progress",
)
data class EpisodeProgressEntity(
    @PrimaryKey(autoGenerate = false) val link: LinkData,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val isCompleted: Boolean = false,
    val lastWatchedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "media_groups",
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["objectId"])
    ]
)
data class MediaGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val objectId: Long,
    val positionInGroup: Int
)

@Serializable
@Parcelize
data class EpisodeInfo(
    val position: Int,
    val name: String,
    val preview: ImageData,
    val link: LinkData
) : Parcelable

@Serializable
@Parcelize
data class ChapterInfo(
    val position: Int,
    val name: String,
    val pages: List<ChapterPageInfo>
) : Parcelable

@Serializable
@Parcelize
data class ChapterPageInfo(
    val position: Int,
    val content: ImageData
) : Parcelable

interface PlayListTypeInterface : Parcelable {
    val displayNameId: Int
}

@Parcelize
@Serializable
enum class PlayListType: PlayListTypeInterface {
    Anime {
        override val displayNameId: Int = R.string.Anime
    },
    Manga {
        override val displayNameId: Int = R.string.Manga
    },
    Music {
        override val displayNameId: Int = R.string.Music
    },
    AnimeNManga {
        override val displayNameId: Int = R.string.AnimeNManga
    }
}

@Serializable
sealed interface ObjectData {
    val id: Long
    val position: Int
    val link: LinkData?
    val isUserCreated: Boolean
    val name: String

    fun injectObjectEntityData(entity: ObjectEntity): ObjectData

    fun copyWithPosition(position: Int): ObjectData

    @Serializable
    @SerialName("carousel")
    data class Carousel(
        override val id: Long = -1,
        override val position: Int = 0,
        override val link: LinkData? = null,
        override val isUserCreated: Boolean = false,
        override val name: String = "",
        val showIco: Boolean = false,
        val ico: ImageData? = null,
        val childsShowName: Boolean = false,
        val childsNamePosition: Int = 0,
        val childsShowAlreadyWatchedLine: Boolean = true,
        val childsShowAuthor: Boolean = false,
        val carouselType: CarouselType = CarouselType.Anime,
        val carouselCollectionType: CollectionType = CollectionType.None,
        val childsCornerRadius: SizeType = SizeType.MEDIUM,
        val childsSize: CardSize = CardSize.MEDIUM,
        val layoutType: LayoutType = LayoutType.DEFAULT,
        val dovodchik: Boolean = false,
        val showDovodchikDots: Boolean = false,
        val objectsInOneLine: Int? = null,
        val maxLines: Int? = null,
        val adaptiveGridSize: Boolean = false,
        val maxObjectsInOneLineForAdaptiveSize: Int? = null,
        val maxLinesForAdaptiveSize: Int? = null
    ) : ObjectData {
        override fun injectObjectEntityData(entity: ObjectEntity): ObjectData {
            return copy(
                id = entity.id,
                position = entity.position,
                link = entity.link,
                isUserCreated = entity.isUserCreated,
                name = entity.name
            )
        }

        override fun copyWithPosition(position: Int): ObjectData {
            return copy(position = position)
        }
    }

    @Serializable
    sealed interface Card : ObjectData {
        val author: String
        val image: ImageData
        val genre: List<GridGenreItem>

        @Serializable
        @SerialName("card_anime")
        data class Anime(
            override val id: Long = -1,
            override val position: Int = 0,
            override val link: LinkData? = null,
            override val isUserCreated: Boolean = false,
            override val name: String = "",
            override val author: String = "",
            override val image: ImageData,
            override val genre: List<GridGenreItem> = emptyList(),
            val description: String = "",
            val episodesList: List<EpisodeInfo> = emptyList(),
        ) : Card {
            override fun injectObjectEntityData(entity: ObjectEntity): ObjectData {
                return copy(
                    id = entity.id,
                    position = entity.position,
                    link = entity.link,
                    isUserCreated = entity.isUserCreated,
                    name = entity.name
                )
            }

            override fun copyWithPosition(position: Int): ObjectData {
                return copy(position = position)
            }
        }

        @Serializable
        @SerialName("card_manga")
        data class Manga(
            override val id: Long = -1,
            override val position: Int = 0,
            override val link: LinkData? = null,
            override val isUserCreated: Boolean = false,
            override val name: String = "",
            override val author: String = "",
            override val image: ImageData,
            override val genre: List<GridGenreItem> = emptyList(),
            val description: String = "",
            val chaptersList: List<ChapterInfo> = emptyList(),
        ) : Card {
            override fun injectObjectEntityData(entity: ObjectEntity): ObjectData {
                return copy(
                    id = entity.id,
                    position = entity.position,
                    link = entity.link,
                    isUserCreated = entity.isUserCreated,
                    name = entity.name
                )
            }

            override fun copyWithPosition(position: Int): ObjectData {
                return copy(position = position)
            }
        }

        @Serializable
        @SerialName("card_music")
        data class Music(
            override val id: Long = -1,
            override val position: Int = 0,
            override val link: LinkData? = null,
            override val isUserCreated: Boolean = false,
            override val name: String = "",
            override val author: String = "",
            override val image: ImageData,
            override val genre: List<GridGenreItem> = emptyList(),
            val song: LinkData? = null,
            val horizontalVideo: LinkData? = null
        ) : Card {
            override fun injectObjectEntityData(entity: ObjectEntity): ObjectData {
                return copy(
                    id = entity.id,
                    position = entity.position,
                    link = entity.link,
                    isUserCreated = entity.isUserCreated,
                    name = entity.name
                )
            }

            override fun copyWithPosition(position: Int): ObjectData {
                return copy(position = position)
            }
        }

        @Serializable
        @SerialName("card_playlist")
        data class Playlist(
            override val id: Long = -1,
            override val position: Int = 0,
            override val link: LinkData? = null,
            override val isUserCreated: Boolean = false,
            override val name: String = "",
            override val author: String = "",
            override val image: ImageData,
            override val genre: List<GridGenreItem> = emptyList(),
            val playListType: PlayListType,
            val cardsList: List<Long> = emptyList()
        ) : Card {
            override fun injectObjectEntityData(entity: ObjectEntity): ObjectData {
                return copy(
                    id = entity.id,
                    position = entity.position,
                    link = entity.link,
                    isUserCreated = entity.isUserCreated,
                    name = entity.name
                )
            }

            override fun copyWithPosition(position: Int): ObjectData {
                return copy(position = position)
            }
        }
    }
}
