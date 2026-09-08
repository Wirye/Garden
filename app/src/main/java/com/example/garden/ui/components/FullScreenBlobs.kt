package com.example.garden.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.garden.ui.theme.windowInfo

@Composable
fun FullScreenBlobs(
    translationY: () -> Float
) {
    Box(
        modifier = Modifier
            .size(width = MaterialTheme.windowInfo.widthDp, height = MaterialTheme.windowInfo.heightDp)
            .background(Color.Transparent)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
                .graphicsLayer {
                    alpha = 0.85f
                    this.translationY = translationY()
                }
        ) {
            val width = size.width
            val height = size.height

            val purpleColor = Color(0xFFA855F7) // Left blob color
            val pinkColor = Color(0xFFEC4899)   // Center blob color
            val orangeColor = Color(0xFFF97316) // Right blob color

            val radius = width * 0.35f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(purpleColor, purpleColor.copy(alpha = 0.8f), Color.Transparent),
                    center = Offset(width * 0.25f, height * 0.12f),
                    radius = radius
                ),
                radius = radius,
                center = Offset(width * 0.25f, height * 0.12f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(pinkColor, pinkColor.copy(alpha = 0.8f), Color.Transparent),
                    center = Offset(width * 0.75f, height * 0.13f),
                    radius = radius
                ),
                radius = radius,
                center = Offset(width * 0.75f, height * 0.13f),
                blendMode = BlendMode.Screen
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(orangeColor, orangeColor.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(width * 0.5f, height * 0.08f),
                    radius = radius * 1.1f
                ),
                radius = radius * 1.1f,
                center = Offset(width * 0.5f, height * 0.08f),
                blendMode = BlendMode.Screen
            )
        }
    }
}