package com.example.garden.ui.utils

import android.util.Log
import com.example.garden.baseDensity
import com.example.garden.database.ElementType
import com.example.garden.objectData2
import com.example.garden.screenWidth
import kotlin.math.ceil
import kotlin.math.round

fun uploadLayoutTypeToCarouselChilds(childs: List<objectData2>, parent: objectData2, customLineWidth: Int? = null): List<objectData2> {
    val layoutType = parent.layoutType
    if (layoutType != 2) return childs
    else if (childs.isEmpty()) return childs
    else if (childs[0].layoutType == 0) return childs
    else {
        val ress = mutableListOf<objectData2>()
        val paddingHorizontal = parent.paddingHorizontal ?: round(19f*baseDensity).toInt()
        val lineWidth = (customLineWidth ?: screenWidth) - (paddingHorizontal*2)
        if (parent.adaptiveGridSize) {
            if (!(childs.any { it.width != null })) {
                Log.d("CarouselsAdapter (uploadLayoutTypeToChilds)", "Childs doesn't have width")
                return emptyList()
            }
            val width = childs.first { it.width != null }.width
            val height = childs.first { it.height != null }.height
            val margin = parent.marginBetweenElementsHorizontal ?: round(4f*baseDensity).toInt()
            val amountOfCards = cardScaleCalcForGrid(width, height, lineWidth, margin, null).third.toInt()
            val res = calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(parent, amountOfCards)
            val maxLines = res.second ?: 1000000
            val objectsInOneLine = res.first
            val amountOfGrids = ceil(childs.size.toFloat() / (objectsInOneLine.toFloat() * maxLines.toFloat())).toInt()
            var currentI = 0
            var currentPosition = 0
            for (o in 0 until amountOfGrids) {
                currentPosition = 0
                val childss = mutableListOf<objectData2>()
                for (i in currentI until currentI + (maxLines * objectsInOneLine)) {
                    if (i <= childs.indices.last) {
                        val child = childs[i]
                        child.position = currentPosition
                        currentPosition += 1
                        childss.add(child)
                        currentI += 1
                    }
                    else break
                }
                val gridWrapper = objectData2(
                    id = 0,
                    page = 0,
                    position = o,
                    childsShowAuthor = parent.childsShowAuthor,
                    childsShowName = parent.childsShowName,
                    childsNamePosition = parent.childsNamePosition,
                    childsCornerRadius = parent.childsCornerRadius,
                    childsShowAlreadyWatchedLine = parent.childsShowAlreadyWatchedLine,
                    alreadyWatched = 0,
                    length = 0,
                    elementType = ElementType.Carousel,
                    childs = childss,
                    layoutType = 0,
                    paddingHorizontal = paddingHorizontal,
                    marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal,
                    marginBetweenElementsVertical = parent.marginBetweenElementsVertical,
                    objectsInOneLine = objectsInOneLine,
                    maxLines = maxLines,
                )
                ress.add(gridWrapper)
            }
        }
        else {
            if (!(childs.any { it.width != null })) {
                Log.d("CarouselsAdapter (uploadLayoutTypeToChilds)", "Childs doesn't have width")
                return emptyList()
            }
            val width = childs.first { it.width != null }.width
            val height = childs.first { it.height != null }.height
            val margin = parent.marginBetweenElementsHorizontal ?: round(4f*baseDensity).toInt()
            val amountOfCards = cardScaleCalcForGrid(width, height, lineWidth, margin, null).third.toInt()
            val objectsInOneLine = parent.objectsInOneLine ?: amountOfCards
            val maxLines = parent.maxLines ?: 1000000
            val amountOfGrids = ceil(childs.size.toFloat() / (objectsInOneLine.toFloat() * maxLines.toFloat())).toInt()
            var currentI = 0
            var currentPosition = 0
            for (o in 0 until amountOfGrids) {
                currentPosition = 0
                val childss = mutableListOf<objectData2>()
                for (i in currentI until currentI + (maxLines * objectsInOneLine)) {
                    if (i <= childs.indices.last) {
                        val child = childs[i]
                        child.position = currentPosition
                        currentPosition += 1
                        childss.add(child)
                        currentI += 1
                    }
                    else break
                }
                val gridWrapper = objectData2(
                    id = 0,
                    page = 0,
                    position = o,
                    childsShowAuthor = parent.childsShowAuthor,
                    childsShowName = parent.childsShowName,
                    childsNamePosition = parent.childsNamePosition,
                    childsCornerRadius = parent.childsCornerRadius,
                    childsShowAlreadyWatchedLine = parent.childsShowAlreadyWatchedLine,
                    alreadyWatched = 0,
                    length = 0,
                    elementType = ElementType.Carousel,
                    childs = childss,
                    layoutType = 0,
                    paddingHorizontal = paddingHorizontal,
                    marginBetweenElementsHorizontal = parent.marginBetweenElementsHorizontal,
                    marginBetweenElementsVertical = parent.marginBetweenElementsVertical,
                    objectsInOneLine = objectsInOneLine,
                    maxLines = parent.maxLines,
                )
                ress.add(gridWrapper)
            }
        }
        return ress
    }
}