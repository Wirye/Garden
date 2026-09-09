package com.example.garden.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.launch

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
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    var isExtraOptionsExpanded by remember { mutableStateOf(false) }
    if (isExtraOptionsExpanded) {
        ExtraOptions(
            onDismiss = { isExtraOptionsExpanded = false },
            onCardDelete = onDelete,
            onCardEdit = onEdit
        )
    }

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
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
                                ?: stringResource(R.string.withoutAuthor)).ifEmpty {
                                stringResource(
                                    R.string.withoutAuthor
                                )
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        isExtraOptionsExpanded = !isExtraOptionsExpanded
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert_ico),
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                }
            }
        }
    } else {
        var isPressed by remember { mutableStateOf(false) }

        val scaleTransition by animateFloatAsState(
            if (isPressed) 0.9f else 1f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        Column(
            modifier = Modifier
                .width(width)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            isExtraOptionsExpanded = true
                        },
                        onTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onClick()
                        }
                    )
                }
                .graphicsLayer {
                    this.scaleX = scaleTransition
                    this.scaleY = scaleTransition
                }
        ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExtraOptions(
    onDismiss: () -> Unit,
    onCardDelete: () -> Unit,
    onCardEdit: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val coroutineScope = rememberCoroutineScope()

    val animateAndDismiss: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onCardEdit()
                        animateAndDismiss()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.screenHorizontal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onCardEdit()
                            animateAndDismiss()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit_ico),
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            contentDescription = null
                        )
                    }

                    Text(
                        modifier = Modifier.weight(1f, fill = false),
                        text = stringResource(R.string.edit),
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }


            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onCardDelete()
                        animateAndDismiss()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.screenHorizontal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onCardDelete()
                            animateAndDismiss()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delete_ico),
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = LocalCustomColors.current.closeButton,
                            contentDescription = null
                        )
                    }

                    Text(
                        modifier = Modifier.weight(1f, fill = false),
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.titleMedium,
                        color = LocalCustomColors.current.closeButton,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
