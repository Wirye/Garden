package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val InfoIco: ImageVector
    get() {
        if (_InfoIco != null) {
            return _InfoIco!!
        }
        _InfoIco = ImageVector.Builder(
            name = "InfoIco",
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
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 4f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(32f, 42.667f)
                    verticalLineTo(32f)
                    moveTo(32f, 21.333f)
                    horizontalLineTo(32.027f)
                    moveTo(58.667f, 32f)
                    curveTo(58.667f, 46.728f, 46.728f, 58.667f, 32f, 58.667f)
                    curveTo(17.272f, 58.667f, 5.333f, 46.728f, 5.333f, 32f)
                    curveTo(5.333f, 17.272f, 17.272f, 5.333f, 32f, 5.333f)
                    curveTo(46.728f, 5.333f, 58.667f, 17.272f, 58.667f, 32f)
                    close()
                }
            }
        }.build()

        return _InfoIco!!
    }

@Suppress("ObjectPropertyName")
private var _InfoIco: ImageVector? = null
