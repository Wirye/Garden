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

val EyeIco: ImageVector
    get() {
        if (_EyeIco != null) {
            return _EyeIco!!
        }
        _EyeIco = ImageVector.Builder(
            name = "EyeIco",
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
                    moveTo(2.667f, 32f)
                    curveTo(2.667f, 32f, 13.333f, 10.667f, 32f, 10.667f)
                    curveTo(50.667f, 10.667f, 61.333f, 32f, 61.333f, 32f)
                    curveTo(61.333f, 32f, 50.667f, 53.333f, 32f, 53.333f)
                    curveTo(13.333f, 53.333f, 2.667f, 32f, 2.667f, 32f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 4f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(32f, 40f)
                    curveTo(36.418f, 40f, 40f, 36.418f, 40f, 32f)
                    curveTo(40f, 27.582f, 36.418f, 24f, 32f, 24f)
                    curveTo(27.582f, 24f, 24f, 27.582f, 24f, 32f)
                    curveTo(24f, 36.418f, 27.582f, 40f, 32f, 40f)
                    close()
                }
            }
        }.build()

        return _EyeIco!!
    }

@Suppress("ObjectPropertyName")
private var _EyeIco: ImageVector? = null
