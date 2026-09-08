package com.example.garden.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.example.garden.R
import com.example.garden.ui.theme.dimens


@Composable
fun AsyncImageWithAddPlaceholder(
    modifier: Modifier = Modifier,
    model: Any?,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier.clip(shape)
    ) {
        val state = painter.state
        if (state is AsyncImagePainter.State.Success && model != null) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    .border(
                        shape = shape,
                        color = MaterialTheme.colorScheme.secondary,
                        width = MaterialTheme.dimens.strokeThin
                    )
            ) {
                Icon(
                    modifier = Modifier
                        .size(MaterialTheme.dimens.iconLarge)
                        .align(Alignment.Center),
                    painter = painterResource(R.drawable.add_ico),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}