package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DragHandleIco: ImageVector
    get() {
        if (_DragHandleIco != null) {
            return _DragHandleIco!!
        }
        _DragHandleIco = ImageVector.Builder(
            name = "DragHandleIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(8.667f, 32.5f)
                verticalLineTo(28.167f)
                horizontalLineTo(43.333f)
                verticalLineTo(32.5f)
                horizontalLineTo(8.667f)
                close()
                moveTo(8.667f, 23.833f)
                verticalLineTo(19.5f)
                horizontalLineTo(43.333f)
                verticalLineTo(23.833f)
                horizontalLineTo(8.667f)
                close()
            }
        }.build()

        return _DragHandleIco!!
    }

@Suppress("ObjectPropertyName")
private var _DragHandleIco: ImageVector? = null
