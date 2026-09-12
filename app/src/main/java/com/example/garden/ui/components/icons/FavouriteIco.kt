package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val FavouriteIco: ImageVector
    get() {
        if (_FavouriteIco != null) {
            return _FavouriteIco!!
        }
        _FavouriteIco = ImageVector.Builder(
            name = "FavouriteIco",
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
                    moveTo(48f, 0f)
                    lineTo(58.777f, 33.167f)
                    horizontalLineTo(93.651f)
                    lineTo(65.437f, 53.666f)
                    lineTo(76.214f, 86.833f)
                    lineTo(48f, 66.334f)
                    lineTo(19.786f, 86.833f)
                    lineTo(30.563f, 53.666f)
                    lineTo(2.349f, 33.167f)
                    horizontalLineTo(37.223f)
                    lineTo(48f, 0f)
                    close()
                }
            }
        }.build()

        return _FavouriteIco!!
    }

@Suppress("ObjectPropertyName")
private var _FavouriteIco: ImageVector? = null
