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

val EyeOffIco: ImageVector
    get() {
        if (_EyeOffIco != null) {
            return _EyeOffIco!!
        }
        _EyeOffIco = ImageVector.Builder(
            name = "EyeOffIco",
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
            }
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
                    moveTo(47.84f, 47.84f)
                    curveTo(43.282f, 51.315f, 37.731f, 53.24f, 32f, 53.333f)
                    curveTo(13.333f, 53.333f, 2.667f, 32f, 2.667f, 32f)
                    curveTo(5.984f, 25.818f, 10.584f, 20.418f, 16.16f, 16.16f)
                    moveTo(26.4f, 11.307f)
                    curveTo(28.236f, 10.877f, 30.115f, 10.662f, 32f, 10.667f)
                    curveTo(50.667f, 10.667f, 61.333f, 32f, 61.333f, 32f)
                    curveTo(59.715f, 35.028f, 57.784f, 37.879f, 55.573f, 40.507f)
                    moveTo(37.653f, 37.653f)
                    curveTo(36.921f, 38.439f, 36.038f, 39.07f, 35.056f, 39.507f)
                    curveTo(34.075f, 39.944f, 33.016f, 40.179f, 31.941f, 40.198f)
                    curveTo(30.867f, 40.217f, 29.8f, 40.02f, 28.804f, 39.617f)
                    curveTo(27.808f, 39.215f, 26.903f, 38.616f, 26.144f, 37.856f)
                    curveTo(25.384f, 37.097f, 24.785f, 36.192f, 24.383f, 35.196f)
                    curveTo(23.98f, 34.2f, 23.783f, 33.133f, 23.802f, 32.058f)
                    curveTo(23.821f, 30.984f, 24.056f, 29.925f, 24.493f, 28.944f)
                    curveTo(24.93f, 27.962f, 25.561f, 27.079f, 26.347f, 26.347f)
                    moveTo(2.667f, 2.667f)
                    lineTo(61.333f, 61.333f)
                }
            }
        }.build()

        return _EyeOffIco!!
    }

@Suppress("ObjectPropertyName")
private var _EyeOffIco: ImageVector? = null
