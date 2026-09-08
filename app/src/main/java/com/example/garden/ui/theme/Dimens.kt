package com.example.garden.ui.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class WindowInfo(
    val widthDp: Dp = 0.dp,
    val heightDp: Dp = 0.dp,
)

data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,       // Стандартный внутренний отступ
    val large: Dp = 24.dp,        // Отступ между крупными секциями
    val extraLarge: Dp = 32.dp,
    val screenHorizontal: Dp = 16.dp, // Отступ от краев экрана
    val marginBetweenElementsInGrid: Dp = 8.dp,
    val dotSpacing: Dp = 6.dp
)

data class Dimens(
    val minTouchTarget: Dp = 48.dp,
    val minButtonHeight: Dp = 48.dp,
    val iconSmall: Dp = 18.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val strokeExtraThin: Dp = 0.7.dp,
    val strokeThin: Dp = 1.dp,
    val strokeThick: Dp = 3.dp,
    val maxBottomSheetWidth: Dp = 640.dp,
    val maxDialogWidth: Dp = 560.dp,
    val maxPopupElementWidth: Dp = 200.dp,
    val topNavHeight: Dp = 100.dp,
    val shadowElevation: Dp = 16.dp,
    val activeDotWidth: Dp = 18.dp,
    val dotSize: Dp = 7.dp,
)

private val tabletDimens = Dimens(maxPopupElementWidth = 400.dp)

val LocalWindowInfo: ProvidableCompositionLocal<WindowInfo> = staticCompositionLocalOf { WindowInfo() }
val LocalSpacing: ProvidableCompositionLocal<Spacing> = staticCompositionLocalOf { Spacing() }
val LocalDimens: ProvidableCompositionLocal<Dimens> = staticCompositionLocalOf { Dimens() }
val LocalTabletDimens: ProvidableCompositionLocal<Dimens> = staticCompositionLocalOf { tabletDimens }