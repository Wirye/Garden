package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AnimeIcoFill: ImageVector
    get() {
        if (_AnimeIcoFill != null) {
            return _AnimeIcoFill!!
        }
        _AnimeIcoFill = ImageVector.Builder(
            name = "AnimeIcoFill",
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
                strokeLineWidth = 18f
            ) {
                moveTo(30f, 15f)
                horizontalLineTo(65f)
                curveTo(73.284f, 15f, 80f, 21.716f, 80f, 30f)
                verticalLineTo(65f)
                curveTo(80f, 73.284f, 73.284f, 80f, 65f, 80f)
                horizontalLineTo(30f)
                curveTo(21.716f, 80f, 15f, 73.284f, 15f, 65f)
                verticalLineTo(30f)
                curveTo(15f, 21.716f, 21.716f, 15f, 30f, 15f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(38f, 57.683f)
                verticalLineTo(35.421f)
                curveTo(38f, 34.612f, 38.911f, 34.138f, 39.574f, 34.601f)
                lineTo(56.27f, 46.289f)
                curveTo(56.855f, 46.699f, 56.835f, 47.571f, 56.232f, 47.953f)
                lineTo(39.535f, 58.528f)
                curveTo(38.869f, 58.95f, 38f, 58.471f, 38f, 57.683f)
                close()
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 19f
            ) {
                moveTo(21.508f, 27.27f)
                curveTo(21.848f, 17.618f, 32.627f, 11.984f, 40.745f, 17.215f)
                lineTo(41.136f, 17.477f)
                lineTo(69.7f, 37.337f)
                curveTo(77.054f, 42.45f, 76.802f, 53.411f, 69.221f, 58.181f)
                lineTo(40.656f, 76.149f)
                curveTo(32.332f, 81.386f, 21.5f, 75.403f, 21.5f, 65.568f)
                verticalLineTo(27.739f)
                lineTo(21.508f, 27.27f)
                close()
            }
        }.build()

        return _AnimeIcoFill!!
    }

@Suppress("ObjectPropertyName")
private var _AnimeIcoFill: ImageVector? = null
