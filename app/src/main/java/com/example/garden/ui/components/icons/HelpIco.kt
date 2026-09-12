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

val HelpIco: ImageVector
    get() {
        if (_HelpIco != null) {
            return _HelpIco!!
        }
        _HelpIco = ImageVector.Builder(
            name = "HelpIco",
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
                    strokeLineWidth = 3f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(24.24f, 24f)
                    curveTo(24.867f, 22.218f, 26.104f, 20.715f, 27.733f, 19.758f)
                    curveTo(29.362f, 18.8f, 31.277f, 18.451f, 33.139f, 18.77f)
                    curveTo(35.001f, 19.089f, 36.69f, 20.057f, 37.907f, 21.503f)
                    curveTo(39.124f, 22.948f, 39.79f, 24.777f, 39.787f, 26.667f)
                    curveTo(39.787f, 32f, 31.787f, 34.667f, 31.787f, 34.667f)
                    moveTo(32f, 45.333f)
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

        return _HelpIco!!
    }

@Suppress("ObjectPropertyName")
private var _HelpIco: ImageVector? = null
