package com.example.garden.ui.utils

import com.example.garden.database.ObjectData
import kotlin.math.round

fun calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(
    parent: ObjectData.Carousel,
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
