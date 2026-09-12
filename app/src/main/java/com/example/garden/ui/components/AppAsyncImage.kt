package com.example.garden.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.garden.LocalCustomColors
import com.example.garden.database.ImageData
import com.example.garden.ui.utils.dataForModel

@Composable
fun AppAsyncImage(
    imageData: ImageData?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    if (imageData == null) {
        Image(
            painter = ColorPainter(LocalCustomColors.current.placeholder),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        when (imageData) {
            is ImageData.Resource -> {
                Image(
                    imageVector = imageData.ico.imageVector,
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = modifier
                )
            }
            is ImageData.Device, is ImageData.Url -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageData.dataForModel())
                        .crossfade(true)
                        .build(),
                    placeholder = ColorPainter(LocalCustomColors.current.placeholder),
                    error = ColorPainter(LocalCustomColors.current.placeholder),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = modifier
                )
            }
        }
    }
}