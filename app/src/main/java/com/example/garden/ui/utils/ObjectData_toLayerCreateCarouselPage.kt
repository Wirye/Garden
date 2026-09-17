package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.ObjectData
import com.example.garden.database.PageType

fun ObjectData.Carousel.toLayerCreateCarouselPage(page: PageType) : Layer.CreateCarouselPage {
    return Layer.CreateCarouselPage(
        carouselId = this.id,
        carouselPosition = this.position,
        name = this.name,
        carouselType = this.carouselType,
        layoutType = this.layoutType,
        page = page,
        childsSize = this.childsSize,
        carouselCollectionType = this.carouselCollectionType,
        childsShowName = this.childsShowName,
        childsShowAuthor = this.childsShowAuthor,
        childsCornerRadius = this.childsCornerRadius,
        childsNamePosition = this.childsNamePosition,
        childsShowAlreadyWatchedLine = this.childsShowAlreadyWatchedLine,
        dovodchik = this.dovodchik,
        showDovodchikDots = this.showDovodchikDots,
        objectsInOneLine = this.objectsInOneLine,
        maxLines = this.maxLines,
        adaptiveGridSize = this.adaptiveGridSize,
        showIco = this.showIco,
        ico = this.ico,
        maxObjectsInOneLineForAdaptiveSize = this.maxObjectsInOneLineForAdaptiveSize,
        maxLinesForAdaptiveSize = this.maxLinesForAdaptiveSize
    )
}