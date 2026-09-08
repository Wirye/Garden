package com.example.garden.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.garden.database.LayoutType
import com.example.garden.database.ObjectData2
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowSizeClass

@Composable
fun cardScaleCalcForGrid(
    width: Dp,
    lineWidth: Dp, // Excluding indents, grid width will be lineWidth - (spacing.screenHorizontal * 2)
    marginBetweenElements: Dp,
    maxObjectsInLine: Int?
): Pair<Dp, Float> {
    var res: Dp
    var amountCards = 0f
    val gridWidth = lineWidth - MaterialTheme.spacing.screenHorizontal
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
