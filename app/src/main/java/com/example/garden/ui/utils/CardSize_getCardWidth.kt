package com.example.garden.ui.utils

import androidx.compose.ui.unit.Dp
import com.example.garden.database.CardSize

fun CardSize.getCardWidth(largeCardWidth: Dp): Dp {
    return when (this) {
        CardSize.SMALL -> (largeCardWidth / 2f)
        CardSize.MEDIUM -> (largeCardWidth / 1.46f)
        CardSize.LARGE -> largeCardWidth
    }
}