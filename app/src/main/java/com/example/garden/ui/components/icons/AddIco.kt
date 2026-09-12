package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AddIco: ImageVector
    get() {
        if (_AddIco != null) {
            return _AddIco!!
        }
        _AddIco = ImageVector.Builder(
            name = "AddIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(23.833f, 28.167f)
                horizontalLineTo(10.833f)
                verticalLineTo(23.833f)
                horizontalLineTo(23.833f)
                verticalLineTo(10.833f)
                horizontalLineTo(28.167f)
                verticalLineTo(23.833f)
                horizontalLineTo(41.167f)
                verticalLineTo(28.167f)
                horizontalLineTo(28.167f)
                verticalLineTo(41.167f)
                horizontalLineTo(23.833f)
                verticalLineTo(28.167f)
                close()
            }
        }.build()

        return _AddIco!!
    }

@Suppress("ObjectPropertyName")
private var _AddIco: ImageVector? = null
