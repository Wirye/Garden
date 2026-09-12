package com.example.garden.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.garden.database.ImageData
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.theme.dimens


@Composable
fun AsyncImageWithAddPlaceholder(
    modifier: Modifier = Modifier,
    model: Any?,
    imageData: ImageData? = null,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    if (imageData != null && imageData is ImageData.Resource) {
        Image(
            imageVector = imageData.ico.imageVector,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build()
        )
        val state = painter.state

        Box(
            modifier = modifier.clip(shape)
        ) {
            if (state is AsyncImagePainter.State.Success && model != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(
                            shape = shape,
                            color = MaterialTheme.colorScheme.secondary,
                            width = MaterialTheme.dimens.strokeThin
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AddIco,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}