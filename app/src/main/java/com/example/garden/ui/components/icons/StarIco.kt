package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val StarIco: ImageVector
    get() {
        if (_StarIco != null) {
            return _StarIco!!
        }
        _StarIco = ImageVector.Builder(
            name = "StarIco",
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
                    moveTo(15.533f, 56.667f)
                    lineTo(19.867f, 37.933f)
                    lineTo(5.333f, 25.333f)
                    lineTo(24.533f, 23.667f)
                    lineTo(32f, 6f)
                    lineTo(39.467f, 23.667f)
                    lineTo(58.667f, 25.333f)
                    lineTo(44.133f, 37.933f)
                    lineTo(48.467f, 56.667f)
                    lineTo(32f, 46.733f)
                    lineTo(15.533f, 56.667f)
                    close()
                }
            }
        }.build()

        return _StarIco!!
    }

@Suppress("ObjectPropertyName")
private var _StarIco: ImageVector? = null
