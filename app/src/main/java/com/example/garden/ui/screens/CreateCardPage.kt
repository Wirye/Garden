package com.example.garden.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.database.CardSize
import com.example.garden.database.ElementType
import com.example.garden.database.ImageData
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.database.ObjectData
import com.example.garden.ui.components.AsyncImageWithAddPlaceholder
import com.example.garden.ui.components.DropDownMenuWithBlur
import com.example.garden.ui.components.GenreEditor
import com.example.garden.ui.components.GenreEditorGenresType
import com.example.garden.ui.components.PopupMenuItem
import com.example.garden.ui.components.SelectableDropDownMenuWithBlur
import com.example.garden.ui.components.SmartFilePicker
import com.example.garden.ui.components.rememberFilePicker
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowInfo
import com.example.garden.ui.theme.windowSizeClass
import com.example.garden.ui.utils.availableCardTypes
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.ui.utils.dataForModel
import com.example.garden.ui.utils.getAspectRatio
import com.example.garden.ui.utils.getCardWidth
import com.example.garden.ui.utils.getLargeCardWidth
import com.example.garden.ui.utils.hazeSourcesForUpperLayers
import com.example.garden.ui.utils.toObjectData
import com.example.garden.utils.getMediaDuration
import com.example.garden.viewmodel.CreateCardViewModel
import com.example.garden.viewmodel.LayersViewModel
import com.example.garden.viewmodel.PageWithSearchSaveOutput
import com.example.garden.viewmodel.ResultSenderViewModel
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
@Composable
fun CreateCardPage(
    layer: Layer.CreateCardPage,
    layersViewModel: LayersViewModel,
    resultSenderViewModel: ResultSenderViewModel,
    onClose: () -> Unit,
    onCloseAndApply: (ObjectData, Layer.CreateCardPage) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val stateViewModel: CreateCardViewModel = viewModel(
        key = layer.id.toString(), factory = CreateCardViewModel.provideFactory(layer)
    )

    val songLength = rememberSaveable { mutableLongStateOf(0L) }

    LaunchedEffect(stateViewModel, layer) {
        stateViewModel.onUpdate = { updated ->
            layer.name = updated.name
            layer.author = updated.author
            layer.description = updated.description
            layer.image = updated.image
            layer.genreList = updated.genreList
            layer.horizontalVideo = updated.horizontalVideo
            layer.verticalVideo = updated.verticalVideo
            layer.song = updated.song
            layer.episodesList = updated.episodesList
            layer.chaptersList = updated.chaptersList
            layer.cardType = updated.cardType
            layer.carouselType = updated.carouselType
            layer.cardsList = updated.cardsList
        }
    }

    val focusManager = LocalFocusManager.current

    val currentPendingEditEpisodesKey = remember { mutableStateOf("") }
    val currentPendingEditChaptersKey = remember { mutableStateOf("") }

    val onEditEpisodes: (String) -> Unit = { key ->
        currentPendingEditEpisodesKey.value = key

        val currentEpisodes = stateViewModel.state.episodesList

        layersViewModel.openLayer(
            layer = Layer.PageWithSearch(
                startsInfo = PageWithSearchInput(
                    items = currentEpisodes,
                    initType = PageWithSearchItemsDefaults.BaseEpisode
                ),
                key = currentPendingEditEpisodesKey.value
            )
        )
    }

    val onEditChapters: (String) -> Unit = { key ->
        currentPendingEditChaptersKey.value = key
        val currentChapters = stateViewModel.state.chaptersList

        layersViewModel.openLayer(
            layer = Layer.PageWithSearch(
                startsInfo = PageWithSearchInput(
                    items = currentChapters,
                    initType = PageWithSearchItemsDefaults.BaseChapter
                ),
                key = currentPendingEditChaptersKey.value
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .blockGestures()
            .clearFocus(focusManager)
            .background(MaterialTheme.colorScheme.background)
    ) {
        val density = LocalDensity.current
        val topInsetPx = WindowInsets.safeDrawing.getTop(density)
        val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
        val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
        val bottomInsetPx = WindowInsets.safeDrawing.getBottom(density)

        val topInset = with(density) { topInsetPx.toDp() }
        val rightInset = with(density) { rightInsetPx.toDp() }
        val leftInset = with(density) { leftInsetPx.toDp() }
        val bottomInset = with(density) { bottomInsetPx.toDp() }

        val width = MaterialTheme.windowInfo.widthDp
        val spacing = MaterialTheme.spacing.medium
        val windowWidthClass = MaterialTheme.windowSizeClass.widthSizeClass
        val windowHeightClass = MaterialTheme.windowSizeClass.heightSizeClass
        val minInputWidth = 200.dp
        val maxInputWidth = 400.dp

        val bannerWidth = remember(windowWidthClass, windowHeightClass, spacing, width) {
            if (width - windowWidthClass.getLargeCardWidth(width) - spacing - minInputWidth >= 0.dp &&
                windowWidthClass != WindowWidthSizeClass.Compact &&
                windowHeightClass != WindowHeightSizeClass.Compact
            ) {
                windowWidthClass.getLargeCardWidth(width)
            } else if (width - CardSize.MEDIUM.getCardWidth(
                    windowWidthClass.getLargeCardWidth(width)
                ) - spacing - minInputWidth >= 0.dp
            ) {
                CardSize.MEDIUM.getCardWidth(windowWidthClass.getLargeCardWidth(width))
            } else {
                CardSize.SMALL.getCardWidth(windowWidthClass.getLargeCardWidth(width))
            }
        }

        val isBannerIsSingle = remember(
            bannerWidth,
            spacing,
            width
        ) { width - bannerWidth - spacing - minInputWidth < 0.dp }
        val isDescriptionInOneLineWithName =
            remember(bannerWidth, windowWidthClass, windowHeightClass, width) {
                (width - bannerWidth - spacing * 2 - minInputWidth >= minInputWidth) &&
                        windowWidthClass != WindowWidthSizeClass.Compact &&
                        windowHeightClass != WindowHeightSizeClass.Compact
            }
        val isDescriptionExists = remember(stateViewModel, stateViewModel.state.cardType) {
            (stateViewModel.state.cardType == ElementType.AnimeCard || stateViewModel.state.cardType == ElementType.MangaCard)
        }

        val nameState = rememberTextFieldState(initialText = stateViewModel.state.name)
        val authorState = rememberTextFieldState(initialText = stateViewModel.state.author)
        val descriptionState =
            rememberTextFieldState(initialText = stateViewModel.state.description)

        LaunchedEffect(nameState) {
            snapshotFlow {
                nameState.text.toString()
            }.distinctUntilChanged()
                .collect { text ->
                    layer.name = text
                }
        }

        LaunchedEffect(authorState) {
            snapshotFlow {
                authorState.text.toString()
            }.distinctUntilChanged()
                .collect { text ->
                    layer.author = text
                }
        }

        LaunchedEffect(descriptionState) {
            snapshotFlow {
                descriptionState.text.toString()
            }.distinctUntilChanged()
                .collect { text ->
                    layer.description = text
                }
        }

        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = stateViewModel.state.firstElementPosition,
            initialFirstVisibleItemScrollOffset = 0
        )

        LaunchedEffect(listState) {
            snapshotFlow {
                val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
                firstVisibleItem?.index
            }.distinctUntilChanged()
                .filterNotNull()
                .collect { holderIndex ->
                    layer.firstElementPosition = holderIndex
                }
        }

        val isBannerImageChangeDialogExpanded = remember { mutableStateOf(false) }

        val openBannerImagePicker = rememberFilePicker(
            mimeTypes = arrayOf("image/*")
        ) { uri ->
            if (uri != null) {
                stateViewModel.update {
                    copy(
                        image = ImageData.Device(uri.toString())
                    )
                }
            }
        }

        SmartFilePicker(
            expanded = isBannerImageChangeDialogExpanded.value,
            onDismiss = { isBannerImageChangeDialogExpanded.value = false },
            onFileDelete = {
                isBannerImageChangeDialogExpanded.value = false
                stateViewModel.update {
                    copy(
                        image = null
                    )
                }
            },
            onFileChange = {
                isBannerImageChangeDialogExpanded.value = false
                openBannerImagePicker()
            },
            hazeState = LocalHazeLayers.current.mainScreen
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = MaterialTheme.spacing.screenHorizontal + topInset,
                    bottom = 0.dp,
                    start = MaterialTheme.spacing.screenHorizontal + leftInset,
                    end = MaterialTheme.spacing.screenHorizontal + rightInset
                ),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = {
                        focusManager.clearFocus()
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onClose()
                    }, colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = LocalCustomColors.current.closeButton,
                        contentColor = LocalCustomColors.current.onCloseButton
                    ), shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close_ico),
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                    )
                }

                val isCardTypeSelectMenuOpened = remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    focusManager.clearFocus()
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    isCardTypeSelectMenuOpened.value = true
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.creatingCard),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                        Icon(
                            painter = painterResource(R.drawable.chevron_forward),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(MaterialTheme.dimens.iconLarge)
                                .rotate(90f)
                        )
                    }

                    val availableCardTypes = remember(stateViewModel.state.carouselType) {
                        mutableStateOf(stateViewModel.state.carouselType.availableCardTypes())
                    }

                    val selectedIndex =
                        remember(stateViewModel.state.cardType, availableCardTypes) {
                            {
                                if (availableCardTypes.value.indexOf(stateViewModel.state.cardType) != -1) {
                                    availableCardTypes.value.indexOf(stateViewModel.state.cardType)
                                } else {
                                    0
                                }
                            }
                        }

                    SelectableDropDownMenuWithBlur(
                        expanded = { isCardTypeSelectMenuOpened.value },
                        onDismissRequest = { isCardTypeSelectMenuOpened.value = false },
                        hazeState = LocalHazeLayers.current.mainScreen,
                        selectedIndex = selectedIndex(),
                        onSelect = {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            stateViewModel.update { copy(cardType = availableCardTypes.value[it]) }
                        },
                        items = availableCardTypes.value.map { stringResource(it.displayNameId) }
                    )
                }


                val isExtraButtonsMenuOpened = remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        isExtraButtonsMenuOpened.value = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.more_vert_ico),
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
                            text = stringResource(R.string.importFromAniLiberty),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                isExtraButtonsMenuOpened.value = false
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.download_ico_2),
                                    contentDescription = null,
                                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                                )
                            }
                        )
                    }
                }
            }

            val bannerShape = MaterialTheme.shapes.medium

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    bottom = MaterialTheme.spacing.extraLarge +
                            MaterialTheme.dimens.minButtonHeight + bottomInset
                )
            ) {
                if (isBannerIsSingle) {
                    item("banner") {
                        AsyncImageWithAddPlaceholder(
                            model = stateViewModel.state.image?.dataForModel(),
                            shape = bannerShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(stateViewModel.state.cardType.getAspectRatio())
                                .clickable(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    isBannerImageChangeDialogExpanded.value = true
                                })
                                .hazeSource(LocalHazeLayers.current.mainScreen)
                                .hazeSourcesForUpperLayers(
                                    LocalHazeStates.current.hazeStates, LocalLayerIndex.current
                                )
                        )
                    }
                }

                item("baseSettings") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.height(IntrinsicSize.Max)
                    ) {
                        val bannerHeightState = remember { mutableStateOf(0.dp) }
                        if (!isBannerIsSingle) {
                            AsyncImageWithAddPlaceholder(
                                model = stateViewModel.state.image?.dataForModel(),
                                shape = bannerShape,
                                modifier = Modifier
                                    .width(bannerWidth)
                                    .aspectRatio(stateViewModel.state.cardType.getAspectRatio())
                                    .clickable(onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        isBannerImageChangeDialogExpanded.value = true
                                    })
                                    .hazeSource(LocalHazeLayers.current.mainScreen)
                                    .hazeSourcesForUpperLayers(
                                        LocalHazeStates.current.hazeStates, LocalLayerIndex.current
                                    )
                            )
                        }

                        val columnHeightState = remember { mutableStateOf(0.dp) }

                        Column(
                            modifier = Modifier
                                .widthIn(minInputWidth, maxInputWidth)
                                .weight(1f, fill = true),
                            verticalArrangement = Arrangement.spacedBy(spacing)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                state = nameState,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.title),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                contentPadding = PaddingValues(MaterialTheme.spacing.small),
                                lineLimits = TextFieldLineLimits.SingleLine
                            )

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                state = authorState,
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.author),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                contentPadding = PaddingValues(MaterialTheme.spacing.small),
                                lineLimits = TextFieldLineLimits.SingleLine
                            )

                            val isSelected = remember(
                                stateViewModel,
                                stateViewModel.state.genreList
                            ) { stateViewModel.state.genreList.isNotEmpty() }

                            val containerColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                                label = "containerColor"
                            )
                            val contentColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                label = "contentColor"
                            )

                            var isBottomSheetOpen by rememberSaveable { mutableStateOf(false) }

                            val genreType = remember(stateViewModel.state.cardType) {
                                mutableStateOf(
                                    if (stateViewModel.state.cardType == ElementType.MusicCard) {
                                        GenreEditorGenresType.MUSIC
                                    } else {
                                        GenreEditorGenresType.ANIME
                                    }
                                )
                            }

                            if (isBottomSheetOpen) {
                                GenreEditor(
                                    onDismiss = { isBottomSheetOpen = false },
                                    onApply = { newGenres ->
                                        stateViewModel.update { copy(genreList = newGenres) }
                                    },
                                    initList = stateViewModel.state.genreList,
                                    genreType = genreType.value
                                )
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    isBottomSheetOpen = true
                                    focusManager.clearFocus()
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = containerColor, contentColor = contentColor
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        MaterialTheme.spacing.small
                                    )
                                ) {
                                    AnimatedContent(
                                        targetState = isSelected, label = "icon"
                                    ) { isSelected ->
                                        Icon(
                                            painter = if (isSelected) painterResource(R.drawable.edit_ico) else painterResource(
                                                R.drawable.add_ico
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                                        )
                                    }

                                    Text(
                                        text = if (isSelected) stringResource(R.string.editGenres) else stringResource(
                                            R.string.selectGenres
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }
                            }
                        }

                        if (isDescriptionInOneLineWithName && isDescriptionExists) {
                            OutlinedTextField(
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                modifier = Modifier
                                    .weight(1f, fill = true)
                                    .widthIn(minInputWidth, maxInputWidth)
                                    .then(
                                        if (bannerHeightState.value != 0.dp || columnHeightState.value != 0.dp) {
                                            Modifier.height(
                                                max(
                                                    bannerHeightState.value,
                                                    columnHeightState.value
                                                )
                                            )
                                        } else {
                                            Modifier
                                        }
                                    ),
                                state = descriptionState,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.description),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                contentPadding = PaddingValues(MaterialTheme.spacing.small),
                            )
                        }
                    }
                }

                if (!isDescriptionInOneLineWithName && isDescriptionExists) {
                    item("description") {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(minInputWidth, maxInputWidth)
                                .aspectRatio(16f / 9f),
                            state = descriptionState,
                            shape = MaterialTheme.shapes.medium,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.description),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            contentPadding = PaddingValues(MaterialTheme.spacing.small),
                        )
                    }
                }

                item("contentSettings") {
                    when (stateViewModel.state.cardType) {
                        ElementType.AnimeCard -> {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    focusManager.clearFocus()
                                    onEditEpisodes(ResultKeys.getPageWithSearchKey())
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        MaterialTheme.spacing.small
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.edit_ico),
                                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.editEpisodes),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.weight(1f, fill = false),
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        ElementType.MangaCard -> {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    focusManager.clearFocus()
                                    onEditChapters(ResultKeys.getPageWithSearchKey())
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        MaterialTheme.spacing.small
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.edit_ico),
                                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.editChapters),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }
                            }
                        }

                        ElementType.MusicCard -> {
                            val isHorizontalVideoChangeDialogExpanded =
                                remember { mutableStateOf(false) }

                            val openHorizontalVideoPicker = rememberFilePicker(
                                mimeTypes = arrayOf("video/*")
                            ) { uri ->
                                if (uri != null) {
                                    stateViewModel.update {
                                        copy(
                                            horizontalVideo = LinkData(
                                                type = LinkType.CONTENT,
                                                targetId = null,
                                                contentPath = uri.toString()
                                            )
                                        )
                                    }
                                }
                            }

                            SmartFilePicker(
                                expanded = isHorizontalVideoChangeDialogExpanded.value,
                                onDismiss = { isHorizontalVideoChangeDialogExpanded.value = false },
                                onFileDelete = {
                                    isHorizontalVideoChangeDialogExpanded.value = false
                                    stateViewModel.update {
                                        copy(horizontalVideo = null)
                                    }
                                },
                                onFileChange = {
                                    isHorizontalVideoChangeDialogExpanded.value = false
                                    openHorizontalVideoPicker()
                                },
                                hazeState = LocalHazeLayers.current.mainScreen
                            )

                            val isVerticalVideoChangeDialogExpanded =
                                remember { mutableStateOf(false) }

                            val openVerticalVideoPicker = rememberFilePicker(
                                mimeTypes = arrayOf("audio/*")
                            ) { uri ->
                                if (uri != null) {
                                    stateViewModel.update {
                                        copy(
                                            verticalVideo = LinkData(
                                                type = LinkType.CONTENT,
                                                targetId = null,
                                                contentPath = uri.toString()
                                            )
                                        )
                                    }
                                }
                            }

                            SmartFilePicker(
                                expanded = isVerticalVideoChangeDialogExpanded.value,
                                onDismiss = { isVerticalVideoChangeDialogExpanded.value = false },
                                onFileDelete = {
                                    isVerticalVideoChangeDialogExpanded.value = false
                                    stateViewModel.update {
                                        copy(verticalVideo = null)
                                    }
                                },
                                onFileChange = {
                                    isVerticalVideoChangeDialogExpanded.value = false
                                    openVerticalVideoPicker()
                                },
                                hazeState = LocalHazeLayers.current.mainScreen
                            )

                            val isSongChangeDialogExpanded = remember { mutableStateOf(false) }

                            val coroutineScope = rememberCoroutineScope()
                            val context = LocalContext.current

                            val openSongPicker = rememberFilePicker(
                                mimeTypes = arrayOf("audio/*")
                            ) { uri ->
                                if (uri != null) {
                                    coroutineScope.launch {
                                        stateViewModel.update {
                                            copy(
                                                song = LinkData(
                                                    type = LinkType.CONTENT,
                                                    targetId = null,
                                                    contentPath = uri.toString()
                                                )
                                            )
                                        }

                                        songLength.longValue =
                                            context.getMediaDuration(uri.toString())
                                    }
                                }
                            }

                            SmartFilePicker(
                                expanded = isSongChangeDialogExpanded.value,
                                onDismiss = { isSongChangeDialogExpanded.value = false },
                                onFileDelete = {
                                    isSongChangeDialogExpanded.value = false
                                    stateViewModel.update {
                                        copy(song = null)
                                    }
                                },
                                onFileChange = {
                                    isSongChangeDialogExpanded.value = false
                                    openSongPicker()
                                },
                                hazeState = LocalHazeLayers.current.mainScreen
                            )

                            val isSelected = remember(stateViewModel, stateViewModel.state.song) {
                                mutableStateOf(stateViewModel.state.song != null)
                            }

                            val isSongPlaying = remember { mutableStateOf(false) }

                            val backgroundColor by animateColorAsState(
                                targetValue = if (isSelected.value) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent,
                                animationSpec = tween(durationMillis = 300),
                                label = "backgroundColor"
                            )

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Max)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(backgroundColor)
                                        .animateContentSize(animationSpec = tween(300))
                                        .padding(if (isSelected.value) MaterialTheme.spacing.medium else 0.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                                focusManager.clearFocus()
                                                isSongChangeDialogExpanded.value = true
                                            },
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            contentPadding = PaddingValues(MaterialTheme.spacing.medium),
                                            modifier = Modifier.weight(1f, fill = true)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    MaterialTheme.spacing.small
                                                )
                                            ) {
                                                AnimatedContent(
                                                    targetState = isSelected, label = "icon"
                                                ) { isSelected ->
                                                    Icon(
                                                        painter = if (isSelected.value) painterResource(
                                                            R.drawable.edit_ico
                                                        ) else painterResource(
                                                            R.drawable.add_ico
                                                        ),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                                                    )
                                                }
                                                Text(
                                                    text = if (isSelected.value) stringResource(
                                                        R.string.changeSong
                                                    ) else stringResource(R.string.selectSong),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    modifier = Modifier.weight(1f, fill = false),
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        AnimatedVisibility(
                                            visible = isSelected.value,
                                            enter = fadeIn(tween(300)) + expandHorizontally(
                                                tween(
                                                    300
                                                )
                                            ),
                                            exit = fadeOut(tween(200)) + shrinkHorizontally(
                                                tween(
                                                    200
                                                )
                                            )
                                        ) {
                                            Button(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                                    focusManager.clearFocus()
                                                    isSongPlaying.value = !isSongPlaying.value
                                                },
                                                shape = CircleShape,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                ),
                                                contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        MaterialTheme.spacing.small
                                                    )
                                                ) {
                                                    AnimatedContent(
                                                        targetState = isSongPlaying, label = "icon"
                                                    ) { isSongPlaying ->
                                                        Icon(
                                                            painter = if (isSongPlaying.value) painterResource(
                                                                R.drawable.pause_ico
                                                            ) else painterResource(
                                                                R.drawable.play_arrow_filled_ico
                                                            ),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = isSelected.value,
                                        enter = fadeIn(
                                            tween(
                                                300,
                                                delayMillis = 100
                                            )
                                        ) + expandVertically(tween(300)),
                                        exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                                    focusManager.clearFocus()
                                                    isHorizontalVideoChangeDialogExpanded.value =
                                                        true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = MaterialTheme.shapes.medium,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                ),
                                                contentPadding = PaddingValues(MaterialTheme.spacing.large)
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(
                                                        MaterialTheme.spacing.small
                                                    ),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    AnimatedContent(
                                                        targetState = stateViewModel.state.horizontalVideo != null,
                                                        label = "icon"
                                                    ) { state ->
                                                        Icon(
                                                            painter = if (state) painterResource(
                                                                R.drawable.edit_ico
                                                            ) else painterResource(
                                                                R.drawable.add_ico
                                                            ),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                                                        )
                                                    }
                                                    Text(
                                                        text = stringResource(R.string.HorizontalVideo),
                                                        style = MaterialTheme.typography.labelLarge,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }

                                            Button(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                                    focusManager.clearFocus()
                                                    isVerticalVideoChangeDialogExpanded.value = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = MaterialTheme.shapes.medium,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                ),
                                                contentPadding = PaddingValues(MaterialTheme.spacing.large)
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(
                                                        MaterialTheme.spacing.small
                                                    ),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    AnimatedContent(
                                                        targetState = stateViewModel.state.verticalVideo != null,
                                                        label = "icon"
                                                    ) { state ->
                                                        Icon(
                                                            painter = if (state) painterResource(
                                                                R.drawable.edit_ico
                                                            ) else painterResource(
                                                                R.drawable.add_ico
                                                            ),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                                                        )
                                                    }
                                                    Text(
                                                        text = stringResource(R.string.VerticalVideo),
                                                        style = MaterialTheme.typography.labelLarge,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        ElementType.PlaylistCard -> {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    focusManager.clearFocus()
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        MaterialTheme.spacing.small
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.edit_ico),
                                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.editContent),
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = MaterialTheme.spacing.screenHorizontal + bottomInset,
                        start = MaterialTheme.spacing.screenHorizontal + leftInset,
                        end = MaterialTheme.spacing.screenHorizontal + rightInset
                    )
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        focusManager.clearFocus()
                        onCloseAndApply(
                            stateViewModel.state.toObjectData(songLength.longValue),
                            stateViewModel.state
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = CircleShape,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add_ico),
                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.addCard),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.weight(1f, fill = false),
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        resultSenderViewModel.results.collect { (key, data) ->
            when (key) {
                currentPendingEditEpisodesKey.value -> {
                    val newData = data as? PageWithSearchSaveOutput
                    if (newData != null) {
                        val newEpisodes = newData.items.filterIsInstance<EpisodeInfo>().toList()

                        stateViewModel.update {
                            copy(episodesList = newEpisodes)
                        }
                    }
                    currentPendingEditEpisodesKey.value = ""
                }

                currentPendingEditChaptersKey.value -> {
                    val newData = data as? PageWithSearchSaveOutput
                    if (newData != null) {
                        val newChapters = newData.items.filterIsInstance<ChapterInfo>().toList()

                        stateViewModel.update {
                            copy(chaptersList = newChapters)
                        }
                    }
                    currentPendingEditChaptersKey.value = ""
                }
            }
        }
    }
}
