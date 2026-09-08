package com.example.garden.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ObjectData2(
    val id: Long = 0,

    // Навигация
    var page: PageType,
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
    var childsCornerRadius: SizeType? = null,
    var childsSize: CardSize? = null,

    var layoutType: LayoutType = LayoutType.DEFAULT,

    var dovodchik: Boolean = false,
    var showDovodchikDots: Boolean = false,

    // Для layout type 0
    var objectsInOneLine: Int? = null,
    var maxLines: Int? = null,
    var adaptiveGridSize: Boolean = false,
    var maxObjectsInOneLineForAdaptiveSize: Int? = null,
    var maxLinesForAdaptiveSize: Int? = null,

    var childs: List<ObjectData2>,
    // Ссылка (куда ведет элемент)
    var link: LinkData? = null,

    // Тип элемента (для удобства фильтрации и только для неё, про макет с.м link -> template)
    var elementType: ElementType,
    var genre: List<GridGenreItem>? = null,
) : Parcelable