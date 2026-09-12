package com.example.garden.ui.utils

import androidx.compose.ui.unit.Dp

fun cardScaleCalcForGrid(
    width: Dp,
    lineWidth: Dp, // Excluding indents, grid width will be lineWidth - (spacing.screenHorizontal * 2)
    marginBetweenElements: Dp,
    maxObjectsInLine: Int?,
    spacingMedium: Dp,
): Pair<Dp, Float> {
    var res: Dp
    var amountCards = 0f
    val gridWidth = lineWidth - spacingMedium
    val availableWidth = (gridWidth - (marginBetweenElements * (amountCards - 1)))
    amountCards = maxObjectsInLine?.toFloat() ?: (availableWidth / width)
    res = (availableWidth / amountCards)
    return Pair(res, amountCards)
}

fun convertToStringTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, secs)
        else -> String.format("%02d:%02d", minutes, secs)
    }
}
