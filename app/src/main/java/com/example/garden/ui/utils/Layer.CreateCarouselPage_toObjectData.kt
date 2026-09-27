package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.ObjectData

fun Layer.CreateCarouselPage.toObjectData() : ObjectData.Carousel {
    return ObjectData.Carousel(
        id = carouselId ?: "${EntitySourceType.Local.name}_0",
        position = carouselPosition,
        name = name,
        layoutType = layoutType,
        objectsInOneLine = objectsInOneLine,
        maxLines = maxLines,
        maxObjectsInOneLineForAdaptiveSize = maxObjectsInOneLineForAdaptiveSize,
        maxLinesForAdaptiveSize = maxLinesForAdaptiveSize,
        adaptiveGridSize = adaptiveGridSize,
        carouselCollectionType = carouselCollectionType,
        showIco = showIco,
        ico = ico,
        childsSize = childsSize,
        childsShowName = childsShowName,
        childsShowAuthor = childsShowAuthor,
        childsNamePosition = childsNamePosition,
        childsCornerRadius = childsCornerRadius,
        childsShowAlreadyWatchedLine = childsShowAlreadyWatchedLine,
        carouselType = carouselType,
        dovodchik = dovodchik,
        showDovodchikDots = showDovodchikDots
    )
}
