package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SkipNextFilledIco: ImageVector
    get() {
        if (_SkipNextFilledIco != null) {
            return _SkipNextFilledIco!!
        }
        _SkipNextFilledIco = ImageVector.Builder(
            name = "SkipNextFilledIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(35.75f, 39f)
                verticalLineTo(13f)
                horizontalLineTo(40.083f)
                verticalLineTo(39f)
                horizontalLineTo(35.75f)
                close()
                moveTo(11.917f, 39f)
                verticalLineTo(13f)
                lineTo(31.417f, 26f)
                lineTo(11.917f, 39f)
                close()
            }
        }.build()

        return _SkipNextFilledIco!!
    }

@Suppress("ObjectPropertyName")
private var _SkipNextFilledIco: ImageVector? = null
