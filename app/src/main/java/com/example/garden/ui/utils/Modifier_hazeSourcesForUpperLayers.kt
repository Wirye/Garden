package com.example.garden.ui.utils

import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

fun Modifier.hazeSourcesForUpperLayers(
    hazeStates: List<HazeState>,
    currentIndex: Int
): Modifier {
    if (currentIndex >= hazeStates.lastIndex) return this

    val upperStates = hazeStates.subList(currentIndex + 1, hazeStates.size)
    return upperStates.fold(this) { accModifier, hazeState ->
        accModifier.hazeSource(hazeState)
    }
}