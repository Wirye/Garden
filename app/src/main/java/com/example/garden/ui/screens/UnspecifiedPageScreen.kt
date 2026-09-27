package com.example.garden.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.LayoutType
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.SizeType
import com.example.garden.ui.components.Card
import com.example.garden.ui.components.DropDownMenuWithBlur
import com.example.garden.ui.components.HelpDialog
import com.example.garden.ui.components.PopupMenuItem
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.HelpIco
import com.example.garden.ui.components.icons.MoreVertIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.ui.utils.getAspectRatio
import com.example.garden.utils.getElementType
import com.example.garden.utils.toLongId
import com.example.garden.viewmodel.MainViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun UnspecifiedPageScreen(
    mainViewModel: MainViewModel,
    layer: Layer.UnspecifiedPage,
    closeLayer: () -> Unit,
    onClickCard: (ObjectData.Card) -> Unit,
    openEditCardPage: (ObjectData.Card) -> Unit,
    deleteCard: (String) -> Unit,
    openCreateCardPage: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current

    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
    val bottomInsetPx = WindowInsets.safeDrawing.getBottom(density)

    val topInset = with(density) { topInsetPx.toDp() }
    val rightInset = with(density) { rightInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }
    val bottomInset = with(density) { bottomInsetPx.toDp() }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = layer.firstElementPosition)

    LaunchedEffect(listState, layer.id) {
        snapshotFlow {
            val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
            firstVisibleItem?.index
        }.distinctUntilChanged()
            .filterNotNull()
            .collect { holderIndex ->
                layer.firstElementPosition = holderIndex
            }
    }

    val spacing = MaterialTheme.spacing.medium

    val cards: LazyPagingItems<ObjectData.Card> =
        mainViewModel.getUnspecifiedPageObjects().collectAsLazyPagingItems()

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .blockGestures()
            .clearFocus(focusManager)
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                bottom = MaterialTheme.spacing.extraLarge +
                        MaterialTheme.dimens.minButtonHeight + bottomInset,
                start = (MaterialTheme.spacing.screenHorizontal / 2) + leftInset,
                end = (MaterialTheme.spacing.screenHorizontal / 2) + rightInset
            )
        ) {
            item("topBarSpacer") {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.spacing.screenHorizontal + topInset - spacing)
                )
            }

            item("topBarHeight") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight))

                    Text(
                        text = stringResource(R.string.Saved),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight))
                }
            }

            if (cards.itemCount == 0) {
                item("nothingIsHere") {
                    Text(
                        text = stringResource(R.string.nothingIsHere),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                        modifier = Modifier.fillMaxSize(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(
                    count = cards.itemCount,
                    key = cards.itemKey { if (it.id.contains("0") || it.id.contains("-1")) "temp_${it.hashCode()}" else it.id.toLongId() },
                    contentType = { "card" }
                ) { index ->
                    val item = cards[index]

                    if (item != null) {
                        val elType = item.getElementType()
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            name = item.name,
                            authors = if (item is ObjectData.Card.Music) item.authors else listOf(item.author),
                            isAnimeCard = item is ObjectData.Card.Anime,
                            image = item.image,
                            aspectRatio = elType.getAspectRatio(),
                            isCircleBanner = elType == ElementType.ArtistCard,
                            showName = true,
                            showAuthor = true,
                            namePosition = 1,
                            alreadyWatched = 0L,
                            length = 0L,
                            showAlreadyWatchedLine = false,
                            cornerRadius = SizeType.SMALL,
                            layoutType = LayoutType.FLAT_GRID_ITEM,
                            isAuthorExists = elType != ElementType.ArtistCard,
                            isSupportEditing = true,
                            isSupportDeleting = true,
                            onClick = { onClickCard(item) },
                            onEdit = { openEditCardPage(item) },
                            onDelete = { deleteCard(item.id) }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = MaterialTheme.spacing.screenHorizontal + topInset,
                    start = MaterialTheme.spacing.screenHorizontal + leftInset
                )
        ) {
            FilledIconButton(
                onClick = {
                    focusManager.clearFocus()
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    closeLayer()
                }, colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = LocalCustomColors.current.closeButton,
                    contentColor = LocalCustomColors.current.onCloseButton
                ), shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = CloseIco,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                )
            }
        }

        var isHelpDialogExpanded by rememberSaveable { mutableStateOf(false) }
        if (isHelpDialogExpanded) {
            HelpDialog(
                title = stringResource(R.string.AboutCreateCarousel),
                message = stringResource(R.string.AboutSavedPageText)
            ) {
                isHelpDialogExpanded = false
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = topInset + MaterialTheme.spacing.screenHorizontal,
                    end = MaterialTheme.spacing.screenHorizontal + rightInset
                )
        ) {
            val isExtraButtonsMenuOpened = remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = {
                    focusManager.clearFocus()
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    isExtraButtonsMenuOpened.value = true
                }) {
                    Icon(
                        imageVector = MoreVertIco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                    )
                }

                DropDownMenuWithBlur(
                    expanded = { isExtraButtonsMenuOpened.value },
                    onDismissRequest = { isExtraButtonsMenuOpened.value = false },
                    hazeState = LocalHazeLayers.current.mainScreen
                ) {
                    PopupMenuItem(
                        text = stringResource(R.string.addCard),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            openCreateCardPage()
                            isExtraButtonsMenuOpened.value = false
                        }
                    ) {
                        Icon(
                            imageVector = AddIco,
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }

                    PopupMenuItem(
                        text = stringResource(R.string.LearnMore),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            isHelpDialogExpanded = true
                            isExtraButtonsMenuOpened.value = false
                        }
                    ) {
                        Icon(
                            imageVector = HelpIco,
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
