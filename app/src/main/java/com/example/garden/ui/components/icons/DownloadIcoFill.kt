package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DownloadIcoFill: ImageVector
    get() {
        if (_DownloadIcoFill != null) {
            return _DownloadIcoFill!!
        }
        _DownloadIcoFill = ImageVector.Builder(
            name = "DownloadIcoFill",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            path(
                fill = SolidColor(Color.White),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(7f, 47.5f)
                curveTo(7f, 44.333f, 8.9f, 38f, 16.5f, 38f)
                curveTo(19.667f, 38f, 26f, 39.9f, 26f, 47.5f)
                curveTo(26f, 50.667f, 24.1f, 57f, 16.5f, 57f)
                curveTo(13.333f, 57f, 7f, 55.1f, 7f, 47.5f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(69f, 47.5f)
                curveTo(69f, 44.333f, 70.9f, 38f, 78.5f, 38f)
                curveTo(81.667f, 38f, 88f, 39.9f, 88f, 47.5f)
                curveTo(88f, 50.667f, 86.1f, 57f, 78.5f, 57f)
                curveTo(75.333f, 57f, 69f, 55.1f, 69f, 47.5f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(38f, 47.5f)
                curveTo(38f, 44.333f, 39.9f, 38f, 47.5f, 38f)
                curveTo(50.667f, 38f, 57f, 39.9f, 57f, 47.5f)
                curveTo(57f, 50.667f, 55.1f, 57f, 47.5f, 57f)
                curveTo(44.333f, 57f, 38f, 55.1f, 38f, 47.5f)
                close()
            }
        }.build()

        return _DownloadIcoFill!!
    }

@Suppress("ObjectPropertyName")
private var _DownloadIcoFill: ImageVector? = null
