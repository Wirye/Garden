package com.example.garden.database

import androidx.compose.runtime.Immutable

@Immutable
data class ObjectData2(
    val id: Long = 0,

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

    // Для layout type 0
    val objectsInOneLine: Int? = null,
    val maxLines: Int? = null,
    val adaptiveGridSize: Boolean = false,
    val maxObjectsInOneLineForAdaptiveSize: Int? = null,
    val maxLinesForAdaptiveSize: Int? = null,

    val childs: List<ObjectData2>,
    // Ссылка (куда ведет элемент)
    val link: LinkData? = null,

    // Тип элемента (для удобства фильтрации и только для неё, про макет с.м link -> template)
    val elementType: ElementType,
    val genre: List<GridGenreItem>? = null,
)
