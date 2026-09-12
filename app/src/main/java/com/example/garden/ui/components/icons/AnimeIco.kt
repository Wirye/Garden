package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AnimeIco: ImageVector
    get() {
        if (_AnimeIco != null) {
            return _AnimeIco!!
        }
        _AnimeIco = ImageVector.Builder(
            name = "AnimeIco",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(6f, 69f)
                verticalLineTo(26f)
                curveTo(6f, 14.954f, 14.954f, 6f, 26f, 6f)
                horizontalLineTo(69f)
                curveTo(80.046f, 6f, 89f, 14.954f, 89f, 26f)
                verticalLineTo(69f)
                curveTo(89f, 80.046f, 80.046f, 89f, 69f, 89f)
                horizontalLineTo(26f)
                curveTo(14.954f, 89f, 6f, 80.046f, 6f, 69f)
                close()
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(37f, 59.683f)
                verticalLineTo(35.421f)
                curveTo(37f, 34.612f, 37.911f, 34.138f, 38.574f, 34.601f)
                lineTo(56.77f, 47.339f)
                curveTo(57.355f, 47.749f, 57.335f, 48.621f, 56.732f, 49.003f)
                lineTo(38.535f, 60.528f)
                curveTo(37.869f, 60.95f, 37f, 60.471f, 37f, 59.683f)
                close()
            }
        }.build()

        return _AnimeIco!!
    }

@Suppress("ObjectPropertyName")
private var _AnimeIco: ImageVector? = null
