package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PauseIco: ImageVector
    get() {
        if (_PauseIco != null) {
            return _PauseIco!!
        }
        _PauseIco = ImageVector.Builder(
            name = "PauseIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(30.333f, 41.167f)
                verticalLineTo(10.833f)
                horizontalLineTo(39f)
                verticalLineTo(41.167f)
                horizontalLineTo(30.333f)
                close()
                moveTo(13f, 41.167f)
                verticalLineTo(10.833f)
                horizontalLineTo(21.667f)
                verticalLineTo(41.167f)
                horizontalLineTo(13f)
                close()
            }
        }.build()

        return _PauseIco!!
    }

@Suppress("ObjectPropertyName")
private var _PauseIco: ImageVector? = null
