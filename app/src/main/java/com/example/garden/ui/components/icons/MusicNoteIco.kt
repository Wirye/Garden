package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MusicNoteIco: ImageVector
    get() {
        if (_MusicNoteIco != null) {
            return _MusicNoteIco!!
        }
        _MusicNoteIco = ImageVector.Builder(
            name = "MusicNoteIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(21.667f, 45.5f)
                curveTo(19.283f, 45.5f, 17.243f, 44.651f, 15.546f, 42.954f)
                curveTo(13.849f, 41.257f, 13f, 39.217f, 13f, 36.833f)
                curveTo(13f, 34.45f, 13.849f, 32.41f, 15.546f, 30.712f)
                curveTo(17.243f, 29.015f, 19.283f, 28.167f, 21.667f, 28.167f)
                curveTo(22.497f, 28.167f, 23.256f, 28.275f, 23.942f, 28.492f)
                curveTo(24.664f, 28.672f, 25.35f, 28.961f, 26f, 29.358f)
                verticalLineTo(6.5f)
                horizontalLineTo(39f)
                verticalLineTo(15.167f)
                horizontalLineTo(30.333f)
                verticalLineTo(36.833f)
                curveTo(30.333f, 39.217f, 29.485f, 41.257f, 27.788f, 42.954f)
                curveTo(26.09f, 44.651f, 24.05f, 45.5f, 21.667f, 45.5f)
                close()
            }
        }.build()

        return _MusicNoteIco!!
    }

@Suppress("ObjectPropertyName")
private var _MusicNoteIco: ImageVector? = null
