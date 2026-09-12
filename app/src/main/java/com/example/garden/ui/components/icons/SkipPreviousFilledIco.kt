package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SkipPreviousFilledIco: ImageVector
    get() {
        if (_SkipPreviousFilledIco != null) {
            return _SkipPreviousFilledIco!!
        }
        _SkipPreviousFilledIco = ImageVector.Builder(
            name = "SkipPreviousFilledIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(11.917f, 39f)
                verticalLineTo(13f)
                horizontalLineTo(16.25f)
                verticalLineTo(39f)
                horizontalLineTo(11.917f)
                close()
                moveTo(40.083f, 39f)
                lineTo(20.583f, 26f)
                lineTo(40.083f, 13f)
                verticalLineTo(39f)
                close()
            }
        }.build()

        return _SkipPreviousFilledIco!!
    }

@Suppress("ObjectPropertyName")
private var _SkipPreviousFilledIco: ImageVector? = null
