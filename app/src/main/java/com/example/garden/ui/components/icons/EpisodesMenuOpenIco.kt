package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EpisodesMenuOpenIco: ImageVector
    get() {
        if (_EpisodesMenuOpenIco != null) {
            return _EpisodesMenuOpenIco!!
        }
        _EpisodesMenuOpenIco = ImageVector.Builder(
            name = "EpisodesMenuOpenIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6.5f, 39f)
                verticalLineTo(34.667f)
                horizontalLineTo(34.667f)
                verticalLineTo(39f)
                horizontalLineTo(6.5f)
                close()
                moveTo(42.467f, 36.833f)
                lineTo(31.633f, 26f)
                lineTo(42.467f, 15.167f)
                lineTo(45.5f, 18.2f)
                lineTo(37.7f, 26f)
                lineTo(45.5f, 33.8f)
                lineTo(42.467f, 36.833f)
                close()
                moveTo(6.5f, 28.167f)
                verticalLineTo(23.833f)
                horizontalLineTo(28.167f)
                verticalLineTo(28.167f)
                horizontalLineTo(6.5f)
                close()
                moveTo(6.5f, 17.333f)
                verticalLineTo(13f)
                horizontalLineTo(34.667f)
                verticalLineTo(17.333f)
                horizontalLineTo(6.5f)
                close()
            }
        }.build()

        return _EpisodesMenuOpenIco!!
    }

@Suppress("ObjectPropertyName")
private var _EpisodesMenuOpenIco: ImageVector? = null
