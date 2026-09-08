package com.example.garden.ui.utils

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun WindowWidthSizeClass.getLargeCardWidth(maxCardWidth: Dp): Dp {
    return when (this) {
        WindowWidthSizeClass.Compact -> 320.dp.coerceIn(0.dp, maxCardWidth)
        WindowWidthSizeClass.Medium -> 420.dp.coerceIn(0.dp, maxCardWidth)
        WindowWidthSizeClass.Expanded -> 480.dp.coerceIn(0.dp, maxCardWidth)
        else -> 420.dp.coerceIn(0.dp, maxCardWidth)
    }
}