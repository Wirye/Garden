package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChevronForward: ImageVector
    get() {
        if (_ChevronForward != null) {
            return _ChevronForward!!
        }
        _ChevronForward = ImageVector.Builder(
            name = "ChevronForward",
            defaultWidth = 65.dp,
            defaultHeight = 65.dp,
            viewportWidth = 65f,
            viewportHeight = 65f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(34.125f, 32.5f)
                lineTo(21.667f, 20.042f)
                lineTo(25.458f, 16.25f)
                lineTo(41.708f, 32.5f)
                lineTo(25.458f, 48.75f)
                lineTo(21.667f, 44.958f)
                lineTo(34.125f, 32.5f)
                close()
            }
        }.build()

        return _ChevronForward!!
    }

@Suppress("ObjectPropertyName")
private var _ChevronForward: ImageVector? = null
