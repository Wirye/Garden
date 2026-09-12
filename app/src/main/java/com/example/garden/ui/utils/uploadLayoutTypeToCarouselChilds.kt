package com.example.garden.ui.utils

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.garden.database.ElementType
import com.example.garden.database.LayoutType
import com.example.garden.database.ObjectData2
import com.example.garden.database.PageType
import kotlin.math.ceil
import kotlin.math.round

fun uploadLayoutTypeToCarouselChilds(
    parent: ObjectData2,
    lineWidth: Dp,
    spacingMedium: Dp,
    spacingMarginBetweenElementsInGrid: Dp,
    widthSizeClass: WindowWidthSizeClass,
    spacingScreenHorizontal: Dp,
): List<ObjectData2> {
    val parent = uploadLayoutType(parent)
    val childs = parent.childs
    val layoutType = parent.layoutType
    if (layoutType != LayoutType.CAROUSEL_FROM_GRID && layoutType != LayoutType.CAROUSEL_FROM_FLAT_GRID) return childs
    else if (childs.isEmpty()) return childs
    else if (childs[0].layoutType == LayoutType.CARD_GRID) return childs
    else {
        val ress = mutableListOf<ObjectData2>()
        val lineWidth = when (layoutType) {
            LayoutType.CAROUSEL_FROM_FLAT_GRID -> lineWidth - spacingScreenHorizontal
            else -> lineWidth - (spacingScreenHorizontal * 2)
        }
        if (parent.adaptiveGridSize) {
            if (parent.childsSize == null) {
                Log.e("Carousel (uploadLayoutTypeToChilds)", "Childs doesn't have size")
                return emptyList()
            }
            val cardType = parent.childs.first().elementType
            val width = when (layoutType) {
                LayoutType.CAROUSEL_FROM_FLAT_GRID -> lineWidth
                else -> parent.childsSize.toDp(
                    cardType,
                    lineWidth,
                    widthSizeClass
                ).width
            }

            val margin = when (layoutType) {
                LayoutType.CAROUSEL_FROM_FLAT_GRID -> 0.dp
                else -> spacingMarginBetweenElementsInGrid
            }
            val amountOfCards =
                cardScaleCalcForGrid(width, lineWidth, margin, null, spacingMedium).second.toInt()
            val res = calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(parent, amountOfCards)
            val maxLines = res.second ?: Int.MAX_VALUE
            val objectsInOneLine = res.first
            val amountOfGrids =
                ceil(childs.size.toFloat() / (objectsInOneLine.toFloat() * maxLines.toFloat())).toInt()
            var currentI = 0
            var currentPosition: Int
            var lastId = 0L
            for (o in 0 until amountOfGrids) {
                currentPosition = 0
                val childss = mutableListOf<ObjectData2>()
                for (i in currentI until currentI + (maxLines * objectsInOneLine)) {
                    if (i <= childs.indices.last) {
                        val child = childs[i].copy(
                            position = currentPosition
                        )
                        currentPosition += 1
                        childss.add(child)
                        currentI += 1
                    } else break
                }
                val gridWrapper = ObjectData2(
                    id = lastId,
                    page = PageType.Home,
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
                    layoutType = when (layoutType) {
                        LayoutType.CAROUSEL_FROM_FLAT_GRID -> LayoutType.CARD_FLAT_GRID
                        else -> LayoutType.CARD_GRID
                    },
                    objectsInOneLine = objectsInOneLine,
                    maxLines = maxLines,
                )
                ress.add(gridWrapper)
                lastId += 1L
            }
        } else {
            if (parent.childsSize == null) {
                Log.e("Carousel (uploadLayoutTypeToChilds)", "Childs doesn't have size")
                return emptyList()
            }
            val cardType = parent.childs.first().elementType
            val width = when (layoutType) {
                LayoutType.CAROUSEL_FROM_FLAT_GRID -> lineWidth
                else -> parent.childsSize.toDp(
                    cardType,
                    lineWidth,
                    widthSizeClass
                ).width
            }

            val margin = when (layoutType) {
                LayoutType.CAROUSEL_FROM_FLAT_GRID -> 0.dp
                else -> spacingMarginBetweenElementsInGrid
            }
            val amountOfCards =
                cardScaleCalcForGrid(width, lineWidth, margin, null, spacingMedium).second.toInt()
            val objectsInOneLine = parent.objectsInOneLine ?: amountOfCards
            val maxLines = parent.maxLines ?: Int.MAX_VALUE
            val amountOfGrids =
                ceil(childs.size.toFloat() / (objectsInOneLine.toFloat() * maxLines.toFloat())).toInt()
            var currentI = 0
            var currentPosition: Int
            var lastId = 0L
            for (o in 0 until amountOfGrids) {
                currentPosition = 0
                val childss = mutableListOf<ObjectData2>()
                for (i in currentI until currentI + (maxLines * objectsInOneLine)) {
                    if (i <= childs.indices.last) {
                        val child = childs[i].copy(

                        )
                        currentPosition += 1
                        childss.add(child)
                        currentI += 1
                    } else break
                }
                val gridWrapper = ObjectData2(
                    id = lastId,
                    page = PageType.Home,
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
                    layoutType = when (layoutType) {
                        LayoutType.CAROUSEL_FROM_FLAT_GRID -> LayoutType.CARD_FLAT_GRID
                        else -> LayoutType.CARD_GRID
                    },
                    objectsInOneLine = objectsInOneLine,
                    maxLines = parent.maxLines,
                )
                ress.add(gridWrapper)
                lastId += 1L
            }
        }
        return ress
    }
}

private fun uploadLayoutType(
    parent: ObjectData2
): ObjectData2 {
    if (parent.childs.isEmpty()) return parent
    val layoutType = parent.layoutType
    val isGridMode = parent.childs.first().layoutType !in listOf(
        LayoutType.CARD_GRID,
        LayoutType.CARD_FLAT_GRID,
    )

    val availableLayoutTypes = when (layoutType) {
        LayoutType.DEFAULT -> listOf(LayoutType.DEFAULT)
        LayoutType.CAROUSEL_GRID, LayoutType.CAROUSEL_FROM_GRID -> if (isGridMode) listOf(LayoutType.DEFAULT) else listOf(
            LayoutType.CARD_GRID
        )

        LayoutType.CAROUSEL_FROM_FLAT_GRID -> if (isGridMode) listOf(LayoutType.FLAT_GRID_ITEM) else listOf(
            LayoutType.CARD_FLAT_GRID
        )

        LayoutType.CARD_FLAT_GRID -> listOf(LayoutType.FLAT_GRID_ITEM)
        LayoutType.CARD_GRID -> listOf(LayoutType.DEFAULT)
        LayoutType.FLAT_GRID_ITEM -> listOf()
    }

    val newChilds = mutableListOf<ObjectData2>()

    for (i in parent.childs.indices) {
        val child = parent.childs[i]
        val childLayoutType = child.layoutType
        if (childLayoutType !in availableLayoutTypes) {
            val newChild = child.copy(
                layoutType = availableLayoutTypes[0]
            )
            newChilds.add(newChild)
        } else {
            newChilds.add(child)
        }
    }

    val parent = parent.copy(
        childs = newChilds
    )
    return parent
}

fun calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(
    parent: ObjectData2,
    objectsInOneLine: Int
): Pair<Int, Int?> {
    val objectsInOneLine2 = objectsInOneLine.coerceIn(
        parent.objectsInOneLine ?: 1,
        parent.maxObjectsInOneLineForAdaptiveSize ?: Int.MAX_VALUE
    )
    var maxLines: Int?
    if (objectsInOneLine2 == parent.maxObjectsInOneLineForAdaptiveSize) {
        maxLines = parent.maxLinesForAdaptiveSize
    } else if (objectsInOneLine2 == parent.objectsInOneLine) {
        maxLines = parent.maxLines
    } else if (parent.maxObjectsInOneLineForAdaptiveSize == null && parent.objectsInOneLine == null) {
        maxLines = parent.maxLinesForAdaptiveSize
    } else if (parent.maxObjectsInOneLineForAdaptiveSize != null && parent.objectsInOneLine == null) {
        maxLines = parent.maxLines
    } else if (parent.maxObjectsInOneLineForAdaptiveSize == null) {
        maxLines = parent.maxLinesForAdaptiveSize
    } else {
        val maxObjectsInOneLineForAdaptiveSize = parent.maxObjectsInOneLineForAdaptiveSize
        val objectsInOneLinee = parent.objectsInOneLine!!
        val maxLinesForAdaptiveSize = parent.maxLinesForAdaptiveSize!!
        val maxLiness = parent.maxLines!!
        val steps = maxObjectsInOneLineForAdaptiveSize - objectsInOneLinee
        val currentStep = objectsInOneLine2 - objectsInOneLinee
        val pr = currentStep.toFloat() / steps.toFloat()
        val linesSteps = maxLinesForAdaptiveSize - maxLiness
        val interpolatedLines = linesSteps.toFloat() * pr
        maxLines = round(maxLiness.toFloat() + interpolatedLines).toInt()
    }
    return Pair(objectsInOneLine2, maxLines)
}
