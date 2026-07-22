package com.example.garden.database

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
enum class Genre {
    Drama,
    Comedy,
    Romance,
    EverydayLife,
    School,
    Psychological,
    Shonen,
    ActionMovie,
    MartialArts,
    Action,
    Adventures,
    Shoujo,
    Fantasy,
    Isekai,
    ScienceFiction,
    Cyberpunk,
    Fantastic,
    Supernatural,
    PostApocalypse,
    Detective,
    Thriller,
    Horrors,
    Mysticism,
    Etty,
    Harem,
    Sezon,
    Year,
    Age,
    Episodes,
}
enum class MusicGenre {
    Rock,
    Jazz,
    LoFi,
    Pop,
    Classical,
    Metal,
    Electronic,
    HipHop,
    Country,
    Ambient
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
    var genre: List<Genre>? = null,
)