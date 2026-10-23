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

val LinkIco: ImageVector
    get() {
        if (_LinkIco != null) {
            return _LinkIco!!
        }
        _LinkIco = ImageVector.Builder(
            name = "LinkIco",
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
                    moveTo(26.667f, 34.667f)
                    curveTo(27.812f, 36.198f, 29.273f, 37.465f, 30.951f, 38.381f)
                    curveTo(32.629f, 39.298f, 34.484f, 39.843f, 36.391f, 39.979f)
                    curveTo(38.298f, 40.116f, 40.212f, 39.841f, 42.004f, 39.173f)
                    curveTo(43.795f, 38.505f, 45.422f, 37.459f, 46.773f, 36.107f)
                    lineTo(54.773f, 28.107f)
                    curveTo(57.202f, 25.592f, 58.546f, 22.224f, 58.516f, 18.728f)
                    curveTo(58.485f, 15.232f, 57.083f, 11.888f, 54.611f, 9.416f)
                    curveTo(52.139f, 6.944f, 48.795f, 5.541f, 45.299f, 5.511f)
                    curveTo(41.803f, 5.481f, 38.435f, 6.825f, 35.92f, 9.253f)
                    lineTo(31.333f, 13.813f)
                    moveTo(37.333f, 29.333f)
                    curveTo(36.188f, 27.802f, 34.727f, 26.535f, 33.049f, 25.619f)
                    curveTo(31.371f, 24.702f, 29.516f, 24.157f, 27.609f, 24.02f)
                    curveTo(25.702f, 23.884f, 23.788f, 24.159f, 21.997f, 24.827f)
                    curveTo(20.205f, 25.496f, 18.578f, 26.541f, 17.227f, 27.893f)
                    lineTo(9.227f, 35.893f)
                    curveTo(6.798f, 38.408f, 5.454f, 41.776f, 5.484f, 45.272f)
                    curveTo(5.515f, 48.768f, 6.917f, 52.112f, 9.389f, 54.584f)
                    curveTo(11.861f, 57.056f, 15.205f, 58.459f, 18.701f, 58.489f)
                    curveTo(22.197f, 58.519f, 25.565f, 57.175f, 28.08f, 54.747f)
                    lineTo(32.64f, 50.187f)
                }
            }
        }.build()

        return _LinkIco!!
    }

@Suppress("ObjectPropertyName")
private var _LinkIco: ImageVector? = null
