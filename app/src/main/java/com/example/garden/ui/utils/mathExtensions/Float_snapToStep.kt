package com.example.garden.ui.utils.mathExtensions

import kotlin.math.round

fun Float.snapToStep(startValue: Float, stepSize: Float): Float {
    if (stepSize <= 0f) return this
    val stepsCount = (this - startValue) / stepSize
    val roundedSteps = round(stepsCount)
    val snappedValue = startValue + (roundedSteps * stepSize)
    return snappedValue
}