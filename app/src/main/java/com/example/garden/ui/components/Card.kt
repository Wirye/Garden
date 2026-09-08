package com.example.garden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.database.ImageData
import com.example.garden.database.LayoutType
import com.example.garden.database.SizeType
import com.example.garden.ui.screens.LocalHazeLayers
import com.example.garden.ui.screens.LocalHazeStates
import com.example.garden.ui.screens.LocalLayerIndex
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.dataForModel
import com.example.garden.ui.utils.hazeSourcesForUpperLayers
import com.example.garden.ui.utils.toDp
import com.example.garden.ui.utils.toShape
import dev.chrisbanes.haze.hazeSource

@Composable
fun Card(
    width: Dp,
    aspectRatio: Float, // Final height may be changed due to showName and showAuthor
    showName: Boolean,
    showAuthor: Boolean,
    namePosition: Int,
    name: String? = null,
    author: String? = null,
    image: ImageData? = null,
    alreadyWatched: Long,
    length: Long,
    showAlreadyWatchedLine: Boolean,
    cornerRadius: SizeType,
    layoutType: LayoutType,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    if (layoutType == LayoutType.FLAT_GRID_ITEM) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onClick()
                }
        ) {
            Row(
                modifier = Modifier
                    .width(width)
                    .aspectRatio(32f / 9f)
                    .padding(vertical = MaterialTheme.spacing.screenHorizontal / 2)
                    .padding(start = MaterialTheme.spacing.screenHorizontal / 2),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image?.dataForModel())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatio)
                        .clip(cornerRadius.toShape())
                        .hazeSource(LocalHazeLayers.current.mainScreen)
                        .hazeSourcesForUpperLayers(
                            LocalHazeStates.current.hazeStates,
                            LocalLayerIndex.current
                        )
                )

                Column(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = (name
                            ?: stringResource(R.string.withoutName)).ifEmpty { stringResource(R.string.withoutName) },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = (author
                            ?: stringResource(R.string.withoutAuthor)).ifEmpty { stringResource(R.string.withoutAuthor) },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .width(width)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onClick()
                }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(cornerRadius.toShape())
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image?.dataForModel())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(cornerRadius.toShape())
                        .hazeSource(LocalHazeLayers.current.mainScreen)
                        .hazeSourcesForUpperLayers(
                            LocalHazeStates.current.hazeStates,
                            LocalLayerIndex.current
                        )
                )

                if (showName && namePosition == 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                    ) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )

                        StrokeText(
                            text = (name
                                ?: stringResource(R.string.withoutName)).ifEmpty { stringResource(R.string.withoutName) },
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.small)
                        )
                    }
                }

                if (showAlreadyWatchedLine) {
                    val progress = if (alreadyWatched > 0L && length > 0L) {
                        (alreadyWatched.toFloat() / length.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    }

                    if (progress > 0f) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MaterialTheme.dimens.strokeThick)
                                .align(Alignment.BottomCenter),
                            color = LocalCustomColors.current.closeButton,
                            trackColor = Color.Transparent,
                            drawStopIndicator = {}
                        )
                    }
                }
            }

            var lineCount by remember { mutableIntStateOf(0) }

            if (showName && namePosition == 0) {
                Text(
                    modifier = Modifier,
                    text = (name
                        ?: stringResource(R.string.withoutName)).ifEmpty { stringResource(R.string.withoutName) },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        lineCount = textLayoutResult.lineCount
                    }
                )
            }

            if (showAuthor) {
                Text(
                    text = (author
                        ?: stringResource(R.string.withoutAuthor)).ifEmpty { stringResource(R.string.withoutAuthor) },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (showName && namePosition == 0 && lineCount == 1) {
                val height = MaterialTheme.typography.titleMedium.lineHeight.toDp()

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                )
            }
        }
    }
}