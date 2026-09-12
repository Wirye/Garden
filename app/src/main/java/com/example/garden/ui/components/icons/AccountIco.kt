package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AccountIco: ImageVector
    get() {
        if (_AccountIco != null) {
            return _AccountIco!!
        }
        _AccountIco = ImageVector.Builder(
            name = "AccountIco",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 6f
            ) {
                moveTo(6f, 47.5f)
                curveTo(6f, 33.667f, 14.3f, 6f, 47.5f, 6f)
                curveTo(61.333f, 6f, 89f, 14.3f, 89f, 47.5f)
                curveTo(89f, 61.333f, 80.7f, 89f, 47.5f, 89f)
                curveTo(33.667f, 89f, 6f, 80.7f, 6f, 47.5f)
                close()
            }
        }.build()

        return _AccountIco!!
    }

@Suppress("ObjectPropertyName")
private var _AccountIco: ImageVector? = null