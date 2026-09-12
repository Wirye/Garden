package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SettingsIco: ImageVector
    get() {
        if (_SettingsIco != null) {
            return _SettingsIco!!
        }
        _SettingsIco = ImageVector.Builder(
            name = "SettingsIco",
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
                    moveTo(24.667f, 58.667f)
                    lineTo(23.6f, 50.133f)
                    curveTo(23.022f, 49.911f, 22.478f, 49.644f, 21.967f, 49.333f)
                    curveTo(21.455f, 49.022f, 20.955f, 48.689f, 20.467f, 48.333f)
                    lineTo(12.533f, 51.667f)
                    lineTo(5.2f, 39f)
                    lineTo(12.067f, 33.8f)
                    curveTo(12.022f, 33.489f, 12f, 33.189f, 12f, 32.9f)
                    verticalLineTo(31.1f)
                    curveTo(12f, 30.811f, 12.022f, 30.511f, 12.067f, 30.2f)
                    lineTo(5.2f, 25f)
                    lineTo(12.533f, 12.333f)
                    lineTo(20.467f, 15.667f)
                    curveTo(20.955f, 15.311f, 21.467f, 14.978f, 22f, 14.667f)
                    curveTo(22.533f, 14.356f, 23.067f, 14.089f, 23.6f, 13.867f)
                    lineTo(24.667f, 5.333f)
                    horizontalLineTo(39.333f)
                    lineTo(40.4f, 13.867f)
                    curveTo(40.978f, 14.089f, 41.522f, 14.356f, 42.033f, 14.667f)
                    curveTo(42.544f, 14.978f, 43.044f, 15.311f, 43.533f, 15.667f)
                    lineTo(51.467f, 12.333f)
                    lineTo(58.8f, 25f)
                    lineTo(51.933f, 30.2f)
                    curveTo(51.978f, 30.511f, 52f, 30.811f, 52f, 31.1f)
                    verticalLineTo(32.9f)
                    curveTo(52f, 33.189f, 51.956f, 33.489f, 51.867f, 33.8f)
                    lineTo(58.733f, 39f)
                    lineTo(51.4f, 51.667f)
                    lineTo(43.533f, 48.333f)
                    curveTo(43.044f, 48.689f, 42.533f, 49.022f, 42f, 49.333f)
                    curveTo(41.467f, 49.644f, 40.933f, 49.911f, 40.4f, 50.133f)
                    lineTo(39.333f, 58.667f)
                    horizontalLineTo(24.667f)
                    close()
                    moveTo(32.133f, 41.333f)
                    curveTo(34.711f, 41.333f, 36.911f, 40.422f, 38.733f, 38.6f)
                    curveTo(40.556f, 36.778f, 41.467f, 34.578f, 41.467f, 32f)
                    curveTo(41.467f, 29.422f, 40.556f, 27.222f, 38.733f, 25.4f)
                    curveTo(36.911f, 23.578f, 34.711f, 22.667f, 32.133f, 22.667f)
                    curveTo(29.511f, 22.667f, 27.3f, 23.578f, 25.5f, 25.4f)
                    curveTo(23.7f, 27.222f, 22.8f, 29.422f, 22.8f, 32f)
                    curveTo(22.8f, 34.578f, 23.7f, 36.778f, 25.5f, 38.6f)
                    curveTo(27.3f, 40.422f, 29.511f, 41.333f, 32.133f, 41.333f)
                    close()
                }
            }
        }.build()

        return _SettingsIco!!
    }

@Suppress("ObjectPropertyName")
private var _SettingsIco: ImageVector? = null
