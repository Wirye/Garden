package com.example.garden.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo

val SeedColor = Color(0xFF675496)

val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided")
}

@Composable
fun GardenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    windowSizeClass: WindowSizeClass,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val spacing = Spacing()
    val dimens = Dimens()
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current

    val currentWindowInfo = with(density) {
        WindowInfo(
            widthDp = windowInfo.containerSize.width.toDp(),
            heightDp = windowInfo.containerSize.height.toDp(),
        )
    }

    val colorScheme = when {
        dynamicColor -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> createDynamicDarkColorScheme(SeedColor)
        else -> createDynamicLightColorScheme(SeedColor)
    }

    CompositionLocalProvider(
        LocalSpacing provides spacing,
        LocalDimens provides dimens,
        com.example.garden.ui.theme.LocalWindowInfo provides currentWindowInfo,
        LocalWindowSizeClass provides windowSizeClass,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val MaterialTheme.windowSizeClass: WindowSizeClass
    @Composable
    get() = LocalWindowSizeClass.current

val MaterialTheme.windowInfo: WindowInfo
    @Composable
    get() = com.example.garden.ui.theme.LocalWindowInfo.current

val MaterialTheme.spacing: Spacing
    @Composable
    get() = LocalSpacing.current

val MaterialTheme.dimens: Dimens
    @Composable
    get() = when(MaterialTheme.windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> LocalDimens.current
        else -> LocalTabletDimens.current
    }