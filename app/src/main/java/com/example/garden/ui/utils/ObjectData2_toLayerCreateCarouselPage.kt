package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.CardSize
import com.example.garden.database.CarouselType
import com.example.garden.database.ObjectData2

fun ObjectData2.toLayerCreateCarouselPage(isEditMode: Boolean) : Layer.CreateCarouselPage {
    return Layer.CreateCarouselPage(
        isEditMode = isEditMode,
        carouselId = this.id,
        name = this.name ?: "",
        carouselType = this.carouselType ?: CarouselType.Anime,
        layoutType = this.layoutType,
        page = this.page,
        childsSize = this.childsSize ?: CardSize.MEDIUM,
        carouselCollectionType = this.carouselCollectionType,
        childsShowName = this.childsShowName,
        childsShowAuthor = this.childsShowAuthor,
        childsCornerRadius = this.childsCornerRadius,
        childsNamePosition = this.childsNamePosition,
        childsShowAlreadyWatchedLine = this.showAlreadyWatchedLine,
        dovodchik = this.dovodchik,
        showDovodchikDots = this.showDovodchikDots,
        objectsInOneLine = this.objectsInOneLine,
        maxLines = this.maxLines,
        adaptiveGridSize = this.adaptiveGridSize,
        showIco = this.showIco,
        ico = this.image,
        maxObjectsInOneLineForAdaptiveSize = this.maxObjectsInOneLineForAdaptiveSize,
        maxLinesForAdaptiveSize = this.maxLinesForAdaptiveSize
    )
}