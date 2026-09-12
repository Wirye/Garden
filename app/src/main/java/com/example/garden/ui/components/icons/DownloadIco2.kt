package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DownloadIco2: ImageVector
    get() {
        if (_DownloadIco2 != null) {
            return _DownloadIco2!!
        }
        _DownloadIco2 = ImageVector.Builder(
            name = "DownloadIco2",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(64f)
                    verticalLineToRelative(64f)
                    horizontalLineToRelative(-64f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color.White)) {
                    moveTo(32f, 42.667f)
                    lineTo(18.667f, 29.333f)
                    lineTo(22.4f, 25.467f)
                    lineTo(29.333f, 32.4f)
                    verticalLineTo(10.667f)
                    horizontalLineTo(34.667f)
                    verticalLineTo(32.4f)
                    lineTo(41.6f, 25.467f)
                    lineTo(45.333f, 29.333f)
                    lineTo(32f, 42.667f)
                    close()
                    moveTo(16f, 53.333f)
                    curveTo(14.533f, 53.333f, 13.278f, 52.811f, 12.233f, 51.767f)
                    curveTo(11.189f, 50.722f, 10.667f, 49.467f, 10.667f, 48f)
                    verticalLineTo(40f)
                    horizontalLineTo(16f)
                    verticalLineTo(48f)
                    horizontalLineTo(48f)
                    verticalLineTo(40f)
                    horizontalLineTo(53.333f)
                    verticalLineTo(48f)
                    curveTo(53.333f, 49.467f, 52.811f, 50.722f, 51.767f, 51.767f)
                    curveTo(50.722f, 52.811f, 49.467f, 53.333f, 48f, 53.333f)
                    horizontalLineTo(16f)
                    close()
                }
            }
        }.build()

        return _DownloadIco2!!
    }

@Suppress("ObjectPropertyName")
private var _DownloadIco2: ImageVector? = null
