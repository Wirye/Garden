package com.example.garden.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val SearchIco: ImageVector
    get() {
        if (_SearchIco != null) {
            return _SearchIco!!
        }
        _SearchIco = ImageVector.Builder(
            name = "SearchIco",
            defaultWidth = 52.dp,
            defaultHeight = 52.dp,
            viewportWidth = 52f,
            viewportHeight = 52f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(37.541f, 35.307f)
                lineTo(42.706f, 41.463f)
                arcTo(1.8f, 1.8f, 98.32f, isMoreThanHalf = false, isPositiveArc = true, 42.485f, 43.999f)
                lineTo(42.485f, 43.999f)
                arcTo(1.8f, 1.8f, 98.32f, isMoreThanHalf = false, isPositiveArc = true, 39.949f, 43.777f)
                lineTo(34.783f, 37.621f)
                arcTo(1.8f, 1.8f, 98.32f, isMoreThanHalf = false, isPositiveArc = true, 35.005f, 35.085f)
                lineTo(35.005f, 35.085f)
                arcTo(1.8f, 1.8f, 98.32f, isMoreThanHalf = false, isPositiveArc = true, 37.541f, 35.307f)
                close()
            }
            path(
                stroke = SolidColor(Color.White),
                strokeLineWidth = 4f
            ) {
                moveTo(9f, 22.291f)
                arcToRelative(16.291f, 16.291f, 0f, isMoreThanHalf = true, isPositiveArc = false, 32.583f, 0f)
                arcToRelative(16.291f, 16.291f, 0f, isMoreThanHalf = true, isPositiveArc = false, -32.583f, 0f)
                close()
            }
        }.build()

        return _SearchIco!!
    }

@Suppress("ObjectPropertyName")
private var _SearchIco: ImageVector? = null
