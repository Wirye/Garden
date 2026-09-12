package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PlayIco: ImageVector
    get() {
        if (_PlayIco != null) {
            return _PlayIco!!
        }
        _PlayIco = ImageVector.Builder(
            name = "PlayIco",
            defaultWidth = 96.dp,
            defaultHeight = 96.dp,
            viewportWidth = 96f,
            viewportHeight = 96f
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(96f)
                    verticalLineToRelative(96f)
                    horizontalLineToRelative(-96f)
                    close()
                }
            ) {
                path(fill = SolidColor(Color.White)) {
                    moveTo(96f, 48f)
                    lineTo(24f, 89.569f)
                    verticalLineTo(6.431f)
                    lineTo(96f, 48f)
                    close()
                }
            }
        }.build()

        return _PlayIco!!
    }

@Suppress("ObjectPropertyName")
private var _PlayIco: ImageVector? = null
