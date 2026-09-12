package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EditIco: ImageVector
    get() {
        if (_EditIco != null) {
            return _EditIco!!
        }
        _EditIco = ImageVector.Builder(
            name = "EditIco",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15.667f, 48.333f)
                horizontalLineTo(18.992f)
                lineTo(41.8f, 25.525f)
                lineTo(38.475f, 22.2f)
                lineTo(15.667f, 45.008f)
                verticalLineTo(48.333f)
                close()
                moveTo(11f, 53f)
                verticalLineTo(43.083f)
                lineTo(41.8f, 12.342f)
                curveTo(42.267f, 11.914f, 42.782f, 11.583f, 43.346f, 11.35f)
                curveTo(43.91f, 11.117f, 44.503f, 11f, 45.125f, 11f)
                curveTo(45.747f, 11f, 46.35f, 11.117f, 46.933f, 11.35f)
                curveTo(47.517f, 11.583f, 48.022f, 11.933f, 48.45f, 12.4f)
                lineTo(51.658f, 15.667f)
                curveTo(52.125f, 16.094f, 52.465f, 16.6f, 52.679f, 17.183f)
                curveTo(52.893f, 17.767f, 53f, 18.35f, 53f, 18.933f)
                curveTo(53f, 19.556f, 52.893f, 20.149f, 52.679f, 20.712f)
                curveTo(52.465f, 21.276f, 52.125f, 21.792f, 51.658f, 22.258f)
                lineTo(20.917f, 53f)
                horizontalLineTo(11f)
                close()
                moveTo(40.108f, 23.892f)
                lineTo(38.475f, 22.2f)
                lineTo(41.8f, 25.525f)
                lineTo(40.108f, 23.892f)
                close()
            }
        }.build()

        return _EditIco!!
    }

@Suppress("ObjectPropertyName")
private var _EditIco: ImageVector? = null
