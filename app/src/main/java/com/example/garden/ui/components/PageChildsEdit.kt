package com.example.garden.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.garden.R
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.PageType
import com.example.garden.ui.components.icons.DragHandleIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.bottomSheetAnimateAndDismiss
import com.example.garden.ui.utils.getAspectRatio
import com.example.garden.utils.toLongId
import com.example.garden.viewmodel.MainViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageChildsEdit(
    page: PageType,
    mainViewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSave: (List<ObjectData.Carousel>) -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    val focusManager = LocalFocusManager.current

    val carousels by mainViewModel.getCarouselsByPage(page).collectAsStateWithLifecycle(emptyList())
    LaunchedEffect(carousels) {
        Log.e("CAROUSELS", "$carousels")
    }

    val newCarousels = remember(carousels) {
        carousels
            .filter { it.info is ObjectData.Carousel && it.isUserCreated }
            .map { it.info.injectLocalObjectEntityData(it) as ObjectData.Carousel }
            .toMutableStateList()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val animateAndDismiss: () -> Unit = {
        bottomSheetAnimateAndDismiss(
            coroutineScope = coroutineScope,
            sheetState = sheetState,
            onDismiss = onDismiss
        )
    }

    val listState = rememberLazyListState()

    val reorderableState = rememberReorderableLazyListState(
        listState,
        PaddingValues(0.dp)
    ) { from, to ->
        Log.e(
            "111PageChildsEdit",
            "from: ${from.index}, to: ${to.index}   ${newCarousels.map { Pair(it.position, it.id) }}"
        )

        newCarousels.add(to.index - 2, newCarousels.removeAt(from.index - 2))
        val fromPosition = newCarousels[from.index - 2].position
        val toPosition = newCarousels[to.index - 2].position
        newCarousels[from.index - 2] =
            newCarousels[from.index - 2].copyWithPosition(toPosition) as ObjectData.Carousel
        newCarousels[to.index - 2] =
            newCarousels[to.index - 2].copyWithPosition(fromPosition) as ObjectData.Carousel

        Log.e(
            "PageChildsEdit",
            "from: ${from.index}, to: ${to.index}   ${newCarousels.map { Pair(it.position, it.id) }}"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                state = listState,
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium + MaterialTheme.dimens.minButtonHeight)
            ) {
                item("label") {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.PageChildsEdit),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item("spacer") {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.medium)
                    )
                }

                itemsIndexed(
                    items = newCarousels,
                    key = { _, item -> if (item.id.toLongId() == 0L) "temp_${item.hashCode()}" else item.id }
                ) { _, item ->
                    ReorderableItem(reorderableState, key = item.id) { isDragging ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (!isDragging) {
                                        Modifier.animateItem(
                                            fadeInSpec = tween(250),
                                            fadeOutSpec = tween(250),
                                            placementSpec = tween(250)
                                        )
                                    } else Modifier
                                ),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shadowElevation = if (isDragging) MaterialTheme.dimens.shadowElevation else 0.dp
                        ) {
                            CarouselPreview(
                                dragModifier = Modifier.draggableHandle(
                                    onDragStarted = { focusManager.clearFocus() }
                                ),
                                carousel = item
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.small)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(MaterialTheme.spacing.screenHorizontal)
                    .padding(top = 0.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onSave(newCarousels)
                        animateAndDismiss()
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = stringResource(R.string.Save),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}


@Composable
fun CarouselPreview(
    @SuppressLint("ModifierParameter") dragModifier: Modifier = Modifier,
    carousel: ObjectData.Carousel,
    isSupportDragging: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.flatGridItemHeight)
            .padding(vertical = MaterialTheme.spacing.screenHorizontal / 2)
            .padding(start = MaterialTheme.spacing.screenHorizontal / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val width = (MaterialTheme.dimens.flatGridItemHeight - MaterialTheme.spacing.screenHorizontal) * ElementType.AnimeCard.getAspectRatio()

            AppAsyncImage(
                imageData = carousel.ico,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(width)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small)
            )

            Text(
                text = carousel.name.ifEmpty { stringResource(R.string.withoutName) },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isSupportDragging) {
            Icon(
                imageVector = DragHandleIco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = dragModifier
                    .padding(MaterialTheme.spacing.small)
                    .size(MaterialTheme.dimens.iconMedium)
            )
        }
    }
}
