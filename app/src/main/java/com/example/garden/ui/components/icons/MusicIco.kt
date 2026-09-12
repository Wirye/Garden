package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MusicIco: ImageVector
    get() {
        if (_MusicIco != null) {
            return _MusicIco!!
        }
        _MusicIco = ImageVector.Builder(
            name = "MusicIco",
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
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(48f, 63f)
                curveTo(48f, 66.333f, 46f, 73f, 38f, 73f)
                curveTo(34.667f, 73f, 28f, 71f, 28f, 63f)
                curveTo(28f, 59.667f, 30f, 53f, 38f, 53f)
                curveTo(41.333f, 53f, 48f, 55f, 48f, 63f)
                close()
                moveTo(48f, 63f)
                verticalLineTo(22.667f)
                curveTo(48f, 21.912f, 48.805f, 21.429f, 49.471f, 21.784f)
                lineTo(70.5f, 33f)
            }
        }.build()

        return _MusicIco!!
    }

@Suppress("ObjectPropertyName")
private var _MusicIco: ImageVector? = null
