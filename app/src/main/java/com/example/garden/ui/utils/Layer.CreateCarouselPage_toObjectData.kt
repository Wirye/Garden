package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.ElementType
import com.example.garden.database.ObjectData

fun Layer.CreateCarouselPage.toObjectData() : ObjectData {
    return ObjectData(
        id = carouselId ?: 0,
        position = 0,
        name = name,
        page = page,
        layoutType = layoutType,
        objectsInOneLine = objectsInOneLine,
        maxLines = maxLines,
        maxObjectsInOneLineForAdaptiveSize = maxObjectsInOneLineForAdaptiveSize,
        maxLinesForAdaptiveSize = maxLinesForAdaptiveSize,
        adaptiveGridSize = adaptiveGridSize,
        elementType = ElementType.Carousel,
        carouselCollectionType = carouselCollectionType,
        showIco = showIco,
        image = ico,
        childsSize = childsSize,
        childsShowName = childsShowName,
        childsShowAuthor = childsShowAuthor,
        childsNamePosition = childsNamePosition,
        childsCornerRadius = childsCornerRadius,
        childsShowAlreadyWatchedLine = childsShowAlreadyWatchedLine,
        carouselType = carouselType,
        dovodchik = dovodchik,
        showDovodchikDots = showDovodchikDots,
        length = 0L,
        alreadyWatched = 0L
    )
}