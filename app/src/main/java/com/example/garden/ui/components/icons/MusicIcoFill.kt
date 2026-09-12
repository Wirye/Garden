package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MusicIcoFill: ImageVector
    get() {
        if (_MusicIcoFill != null) {
            return _MusicIcoFill!!
        }
        _MusicIcoFill = ImageVector.Builder(
            name = "MusicIcoFill",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(18f, 6f)
                horizontalLineTo(77f)
                curveTo(83.627f, 6f, 89f, 11.373f, 89f, 18f)
                verticalLineTo(77f)
                curveTo(89f, 83.627f, 83.627f, 89f, 77f, 89f)
                horizontalLineTo(18f)
                curveTo(11.373f, 89f, 6f, 83.627f, 6f, 77f)
                verticalLineTo(18f)
                curveTo(6f, 11.373f, 11.373f, 6f, 18f, 6f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(38f, 50f)
                curveTo(27.6f, 50f, 25f, 58.667f, 25f, 63f)
                curveTo(25f, 73.4f, 33.667f, 76f, 38f, 76f)
                curveTo(48.4f, 76f, 51f, 67.333f, 51f, 63f)
                horizontalLineTo(86f)
                verticalLineTo(86f)
                horizontalLineTo(51.5f)
                horizontalLineTo(38f)
                horizontalLineTo(9f)
                verticalLineTo(9f)
                horizontalLineTo(72f)
                horizontalLineTo(86f)
                verticalLineTo(30.4f)
                verticalLineTo(33f)
                verticalLineTo(40.5f)
                verticalLineTo(62.5f)
                horizontalLineTo(51f)
                verticalLineTo(62f)
                verticalLineTo(26f)
                lineTo(69.3f, 35.75f)
                lineTo(70.161f, 35.995f)
                curveTo(71.897f, 36.196f, 73.56f, 34.716f, 73.5f, 33f)
                curveTo(73.5f, 31.2f, 72.1f, 30.5f, 72f, 30.4f)
                lineTo(51f, 19.2f)
                curveTo(50.5f, 18.9f, 49.722f, 18.66f, 49f, 18.66f)
                curveTo(48f, 18.55f, 45.2f, 19.5f, 45f, 22.5f)
                verticalLineTo(52f)
                curveTo(42.5f, 50.5f, 40.4f, 50f, 38f, 50f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(51f, 62f)
                verticalLineTo(63.5f)
                horizontalLineTo(86f)
                verticalLineTo(62f)
                horizontalLineTo(51f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 4f
            ) {
                moveTo(33f, 63f)
                curveTo(33f, 61.333f, 34f, 58f, 38f, 58f)
                curveTo(39.667f, 58f, 43f, 59f, 43f, 63f)
                curveTo(43f, 64.667f, 42f, 68f, 38f, 68f)
                curveTo(36.333f, 68f, 33f, 67f, 33f, 63f)
                close()
            }
        }.build()

        return _MusicIcoFill!!
    }

@Suppress("ObjectPropertyName")
private var _MusicIcoFill: ImageVector? = null
