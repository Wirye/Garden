package com.example.garden.ui.utils

import com.example.garden.database.ObjectData
import com.example.garden.database.ObjectData2

fun ObjectData2.toObjectData(parentId: Long?) : ObjectData {
    return ObjectData(
        id = id,
        page = page,
        parentId = parentId,
        position = position,
        name = name,
        showAlreadyWatchedLine = showAlreadyWatchedLine,
        showIco = showIco,
        childsShowName = childsShowName,
        childsNamePosition = childsNamePosition,
        childsShowAlreadyWatchedLine = childsShowAlreadyWatchedLine,
        childsShowAuthor = childsShowAuthor,
        image = image,
        description = description,
        author = author,
        type = type,
        alreadyWatched = alreadyWatched,
        length = length,
        childsSize = childsSize,
        childsCornerRadius = childsCornerRadius,
        layoutType = layoutType,
        dovodchik = dovodchik,
        showDovodchikDots = showDovodchikDots,
        objectsInOneLine = objectsInOneLine,
        maxLines = maxLines,
        link = link,
        elementType = elementType,
        genre = genre,
        carouselType = carouselType,
        carouselCollectionType = carouselCollectionType,
        maxObjectsInOneLineForAdaptiveSize = maxObjectsInOneLineForAdaptiveSize,
        maxLinesForAdaptiveSize = maxLinesForAdaptiveSize,
        adaptiveGridSize = adaptiveGridSize
    )
}