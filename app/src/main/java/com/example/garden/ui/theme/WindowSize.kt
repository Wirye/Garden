package com.example.garden.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

enum class WindowType {
    Compact,   // Телефоны
    Medium,    // Планшеты (Portrait), Foldables
    Expanded   // Планшеты (Landscape), Десктоп
}

@Composable
fun rememberWindowType(): WindowType {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    // Получаем ширину контейнера приложения и переводим px в dp
    val screenWidthDp = with(density) {
        windowInfo.containerSize.width.toDp()
    }

    return when {
        screenWidthDp < 600.dp -> WindowType.Compact
        screenWidthDp < 840.dp -> WindowType.Medium
        else -> WindowType.Expanded
    }
}