package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChevronForwardText: ImageVector
    get() {
        if (_ChevronForwardText != null) {
            return _ChevronForwardText!!
        }
        _ChevronForwardText = ImageVector.Builder(
            name = "ChevronForwardText",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(64f)
                    verticalLineToRelative(64f)
                    horizontalLineToRelative(-64f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color.White)) {
                    moveTo(12.6f, 32f)
                    lineTo(0.333f, 19.733f)
                    lineTo(4.067f, 16f)
                    lineTo(20.067f, 32f)
                    lineTo(4.067f, 48f)
                    lineTo(0.333f, 44.267f)
                    lineTo(12.6f, 32f)
                    close()
                }
            }
        }.build()

        return _ChevronForwardText!!
    }

@Suppress("ObjectPropertyName")
private var _ChevronForwardText: ImageVector? = null
