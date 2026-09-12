package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CheckIco: ImageVector
    get() {
        if (_CheckIco != null) {
            return _CheckIco!!
        }
        _CheckIco = ImageVector.Builder(
            name = "CheckIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(20.692f, 39f)
                lineTo(8.342f, 26.65f)
                lineTo(11.429f, 23.563f)
                lineTo(20.692f, 32.825f)
                lineTo(40.571f, 12.946f)
                lineTo(43.658f, 16.033f)
                lineTo(20.692f, 39f)
                close()
            }
        }.build()

        return _CheckIco!!
    }

@Suppress("ObjectPropertyName")
private var _CheckIco: ImageVector? = null
