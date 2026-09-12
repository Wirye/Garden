package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PlayArrowFilledIco: ImageVector
    get() {
        if (_PlayArrowFilledIco != null) {
            return _PlayArrowFilledIco!!
        }
        _PlayArrowFilledIco = ImageVector.Builder(
            name = "PlayArrowFilledIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(17.333f, 41.167f)
                verticalLineTo(10.833f)
                lineTo(41.167f, 26f)
                lineTo(17.333f, 41.167f)
                close()
            }
        }.build()

        return _PlayArrowFilledIco!!
    }

@Suppress("ObjectPropertyName")
private var _PlayArrowFilledIco: ImageVector? = null
