package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CloseIco: ImageVector
    get() {
        if (_CloseIco != null) {
            return _CloseIco!!
        }
        _CloseIco = ImageVector.Builder(
            name = "CloseIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(13.867f, 41.167f)
                lineTo(10.833f, 38.133f)
                lineTo(22.967f, 26f)
                lineTo(10.833f, 13.867f)
                lineTo(13.867f, 10.833f)
                lineTo(26f, 22.967f)
                lineTo(38.133f, 10.833f)
                lineTo(41.167f, 13.867f)
                lineTo(29.033f, 26f)
                lineTo(41.167f, 38.133f)
                lineTo(38.133f, 41.167f)
                lineTo(26f, 29.033f)
                lineTo(13.867f, 41.167f)
                close()
            }
        }.build()

        return _CloseIco!!
    }

@Suppress("ObjectPropertyName")
private var _CloseIco: ImageVector? = null
