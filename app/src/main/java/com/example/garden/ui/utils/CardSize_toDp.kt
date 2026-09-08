package com.example.garden.ui.utils

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import com.example.garden.database.CardSize
import com.example.garden.database.ElementType

data class CardSpecs(
    val width: Dp,
    val aspectRatio: Float,
)

fun CardSize.toDp(cardType: ElementType, maxCardWidth: Dp, windowWidthSizeClass: WindowWidthSizeClass): CardSpecs {
    val aspectRatio = cardType.getAspectRatio()

    val largeWidth = windowWidthSizeClass.getLargeCardWidth(maxCardWidth)

    val width = this.getCardWidth(largeWidth)

    return CardSpecs(width, aspectRatio)
}