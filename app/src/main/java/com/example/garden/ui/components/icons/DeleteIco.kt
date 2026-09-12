package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DeleteIco: ImageVector
    get() {
        if (_DeleteIco != null) {
            return _DeleteIco!!
        }
        _DeleteIco = ImageVector.Builder(
            name = "DeleteIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15.167f, 45.5f)
                curveTo(13.975f, 45.5f, 12.955f, 45.076f, 12.106f, 44.227f)
                curveTo(11.258f, 43.379f, 10.833f, 42.358f, 10.833f, 41.167f)
                verticalLineTo(13f)
                horizontalLineTo(8.667f)
                verticalLineTo(8.667f)
                horizontalLineTo(19.5f)
                verticalLineTo(6.5f)
                horizontalLineTo(32.5f)
                verticalLineTo(8.667f)
                horizontalLineTo(43.333f)
                verticalLineTo(13f)
                horizontalLineTo(41.167f)
                verticalLineTo(41.167f)
                curveTo(41.167f, 42.358f, 40.742f, 43.379f, 39.894f, 44.227f)
                curveTo(39.045f, 45.076f, 38.025f, 45.5f, 36.833f, 45.5f)
                horizontalLineTo(15.167f)
                close()
                moveTo(36.833f, 13f)
                horizontalLineTo(15.167f)
                verticalLineTo(41.167f)
                horizontalLineTo(36.833f)
                verticalLineTo(13f)
                close()
                moveTo(19.5f, 36.833f)
                horizontalLineTo(23.833f)
                verticalLineTo(17.333f)
                horizontalLineTo(19.5f)
                verticalLineTo(36.833f)
                close()
                moveTo(28.167f, 36.833f)
                horizontalLineTo(32.5f)
                verticalLineTo(17.333f)
                horizontalLineTo(28.167f)
                verticalLineTo(36.833f)
                close()
            }
        }.build()

        return _DeleteIco!!
    }

@Suppress("ObjectPropertyName")
private var _DeleteIco: ImageVector? = null
