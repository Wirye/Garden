package com.example.garden.ui.screens

import android.util.Log
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.database.entities.ArtistType
import com.example.garden.database.entities.CardSize
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.PlayListType
import com.example.garden.ui.components.AsyncImageWithAddPlaceholder
import com.example.garden.ui.components.CardChoice
import com.example.garden.ui.components.DropDownMenuWithBlur
import com.example.garden.ui.components.GenreEditor
import com.example.garden.ui.components.GenreEditorGenresType
import com.example.garden.ui.components.PopupMenuItem
import com.example.garden.ui.components.SelectableDropDownMenuWithBlur
import com.example.garden.ui.components.SmartFilePicker
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.components.icons.ChevronForward
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.DownloadIco2
import com.example.garden.ui.components.icons.EditIco
import com.example.garden.ui.components.icons.MoreVertIco
import com.example.garden.ui.components.icons.PauseIco
import com.example.garden.ui.components.icons.PlayArrowFilledIco
import com.example.garden.ui.components.rememberFilePicker
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowInfo
import com.example.garden.ui.theme.windowSizeClass
import com.example.garden.ui.utils.availableCardTypes
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.bottomSheetAnimateAndDismiss
import com.example.garden.ui.utils.clearFocus
import com.example.garden.ui.utils.dataForModel
import com.example.garden.ui.utils.getAspectRatio
import com.example.garden.ui.utils.getCardWidth
import com.example.garden.ui.utils.getLargeCardWidth
import com.example.garden.ui.utils.hazeSourcesForUpperLayers
import com.example.garden.ui.utils.toChapterInfo
import com.example.garden.ui.utils.toEpisodeInfo
import com.example.garden.ui.utils.toObjectData
import com.example.garden.utils.getArtistType
import com.example.garden.viewmodel.CreateCardViewModel
import com.example.garden.viewmodel.LayersViewModel
import com.example.garden.viewmodel.PageWithSearchSaveOutput
import com.example.garden.viewmodel.ResultSenderViewModel
import com.example.garden.viewmodel.SearchViewModel
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun CreateCardPage(
    searchViewModel: SearchViewModel,
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

    val isEditMode by remember(stateViewModel.state.cardId) { mutableStateOf(stateViewModel.state.cardId != null) }

    LaunchedEffect(stateViewModel, layer) {
        stateViewModel.onUpdate = { updated ->
            layer.name = updated.name
            layer.authorsList = updated.authorsList
            layer.description = updated.description
            layer.image = updated.image
            layer.genreList = updated.genreList
            layer.horizontalVideo = updated.horizontalVideo
            layer.song = updated.song
            layer.episodesList = updated.episodesList
            layer.chaptersList = updated.chaptersList
            layer.cardType = updated.cardType
            layer.carouselType = updated.carouselType
            layer.cardsList = updated.cardsList
            layer.playlistType = updated.playlistType
            layer.artistType = updated.artistType
        }
    }

    val focusManager = LocalFocusManager.current

    val context = LocalContext.current

    val player = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    var playing by remember { mutableStateOf(false) }
    LaunchedEffect(playing) {
        if (playing) {
            player.play()
        } else {
            player.pause()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, player) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                playing = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            player.stop()
            player.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
            searchViewModel.clearArtistsSearch()
        }
    }

    val currentSong = stateViewModel.state.song

    LaunchedEffect(currentSong) {
        playing = false
        val mediaUri: String? = when (currentSong) {
            is LinkData.Device -> currentSong.path.takeIf { it.isNotBlank() }
            is LinkData.Url -> currentSong.url.takeIf { it.isNotBlank() }
            else -> null
        }

        if (mediaUri != null) {
            try {
                player.setMediaItem(MediaItem.fromUri(mediaUri))
                player.prepare()
            } catch (_: Exception) {
            }
        } else {
            player.clearMediaItems()
        }
    }

    LaunchedEffect(stateViewModel.state.artistType) {
        if (stateViewModel.state.artistType == null && stateViewModel.state.cardType.getArtistType() != null) {
            stateViewModel.update {
                copy(
                    artistType = stateViewModel.state.cardType.getArtistType()
                )
            }
        }
    }

    val artistSearchFlow = searchViewModel.searchArtistsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    Log.e("ARTISTSEARCH", "$artistSearchFlow")

    val currentPendingEditEpisodesKey = remember { mutableStateOf("") }
    val currentPendingEditChaptersKey = remember { mutableStateOf("") }

    val onEditEpisodes: (String) -> Unit = { key ->
        currentPendingEditEpisodesKey.value = key

        val currentEpisodes = stateViewModel.state.episodesList

        layersViewModel.openLayer(
            layer = Layer.PageWithSearch(
                startsInfo = PageWithSearchInput(
                    items = currentEpisodes.map { it.toEpisodeInfo() },
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
                    items = currentChapters.map { it.toChapterInfo() },
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
        val authorState =
            rememberTextFieldState(initialText = stateViewModel.state.authorsList.joinToString(", "))
        val descriptionState =
            rememberTextFieldState(initialText = stateViewModel.state.description)

        LaunchedEffect(nameState) {
            snapshotFlow {
                nameState.text.toString()
            }.distinctUntilChanged()
                .collect { text ->
                    stateViewModel.update {
                        copy(name = text)
                    }
                }
        }

        LaunchedEffect(authorState) {
            snapshotFlow {
                authorState.text.toString()
            }.distinctUntilChanged()
                .collect {
                    val text = it.replace(";", ",")
                    stateViewModel.update {
                        copy(authorsList = text.split(",").map { text -> text.trim() })
                    }
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

        val bannerShape =
            if (stateViewModel.state.cardType != ElementType.ArtistCard) MaterialTheme.shapes.medium else CircleShape

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
                start = MaterialTheme.spacing.screenHorizontal + leftInset,
                end = MaterialTheme.spacing.screenHorizontal + rightInset
            )
        ) {
            item("topBarSpacer") {
                Spacer(modifier = Modifier.height(topInset + MaterialTheme.spacing.screenHorizontal - spacing))
            }

            item("topBarText") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight))

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
                                imageVector = ChevronForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.iconLarge)
                                    .rotate(90f)
                            )
                        }

                        val availableCardTypes = remember(stateViewModel.state.carouselType) {
                            mutableStateOf(
                                if (!isEditMode) stateViewModel.state.carouselType.availableCardTypes() else listOf(
                                    stateViewModel.state.cardType
                                )
                            )
                        }

                        val selectedIndex =
                            remember(stateViewModel.state.cardType, availableCardTypes) {
                                if (availableCardTypes.value.indexOf(stateViewModel.state.cardType) != -1) {
                                    availableCardTypes.value.indexOf(stateViewModel.state.cardType)
                                } else {
                                    0
                                }
                            }

                        SelectableDropDownMenuWithBlur(
                            expanded = { isCardTypeSelectMenuOpened.value },
                            onDismissRequest = { isCardTypeSelectMenuOpened.value = false },
                            hazeState = LocalHazeLayers.current.mainScreen,
                            selectedIndex = selectedIndex,
                            onSelect = {
                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                stateViewModel.update { copy(cardType = availableCardTypes.value[it]) }
                            },
                            items = availableCardTypes.value.map { stringResource(it.displayNameId) }
                        )
                    }


                    Spacer(modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight))
                }
            }

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
                                    text = stringResource(
                                        if (stateViewModel.state.cardType != ElementType.ArtistCard &&
                                            stateViewModel.state.artistType != ArtistType.Music &&
                                            stateViewModel.state.artistType != ArtistType.Manga) {
                                            R.string.title
                                        } else {
                                            R.string.name
                                        }
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            contentPadding = PaddingValues(MaterialTheme.spacing.small),
                            lineLimits = TextFieldLineLimits.SingleLine
                        )

                        if (stateViewModel.state.cardType != ElementType.ArtistCard) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
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
                                            text = stringResource(
                                                when (stateViewModel.state.cardType) {
                                                    ElementType.MusicCard -> R.string.authors
                                                    ElementType.AnimeCard -> R.string.studio
                                                    else -> R.string.author
                                                }
                                            ),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    contentPadding = PaddingValues(MaterialTheme.spacing.small),
                                    lineLimits = TextFieldLineLimits.SingleLine
                                )

                                if (stateViewModel.state.cardType == ElementType.MusicCard) {
                                    Text(
                                        text = stringResource(R.string.ListTheAuthorsSeparatedByCommas),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                var currentAuthorId by remember { mutableIntStateOf(0) }
                                LaunchedEffect(
                                    stateViewModel.state.authorsList,
                                    authorState.selection
                                ) {
                                    if (stateViewModel.state.authorsList.isNotEmpty()) {
                                        val cursor = authorState.selection.start
                                        val authors = stateViewModel.state.authorsList

                                        Log.e("SADA", "$cursor  $authors")

                                        var currentCursor = authors[0].length + 1
                                        var currentAuthor = authors[0]
                                        for (i in 0 until authors.size - 1) {
                                            if (cursor < currentCursor) {
                                                break
                                            } else {
                                                currentCursor += currentAuthor.length + 1
                                                currentAuthor = authors[i + 1]
                                            }
                                        }

                                        currentAuthorId = authors.indexOf(currentAuthor)

                                        Log.e("SADA", "$currentAuthorId  $currentAuthor")
                                        searchViewModel.searchArtists(
                                            currentAuthor,
                                            listOf(stateViewModel.state.artistType ?: ArtistType.Music)
                                        )
                                    }
                                }

                                ArtistSearch(
                                    artistsFlow = artistSearchFlow.value.map { it.name },
                                    onArtistChange = { newAuthor ->
                                        Log.e("WHATS","")
                                        stateViewModel.update {
                                            copy(
                                                authorsList = authorsList.mapIndexed { index, string ->
                                                    if (index == currentAuthorId) newAuthor else string
                                                }
                                            )
                                        }
                                        authorState.setTextAndPlaceCursorAtEnd(stateViewModel.state.authorsList.joinToString(", "))
                                    }
                                )
                            }
                        }

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

                        if (stateViewModel.state.cardType != ElementType.ArtistCard &&
                            stateViewModel.state.cardType != ElementType.PlaylistCard &&
                            stateViewModel.state.cardType != ElementType.AlbumCard
                        ) {
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
                                            imageVector = if (isSelected) EditIco else AddIco,
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
                                    imageVector = EditIco,
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
                                    imageVector = EditIco,
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
                                        horizontalVideo = LinkData.Device(
                                            path = uri.toString()
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

                        val isSongChangeDialogExpanded = remember { mutableStateOf(false) }

                        val openSongPicker = rememberFilePicker(
                            mimeTypes = arrayOf("audio/*")
                        ) { uri ->
                            if (uri != null) {
                                stateViewModel.update {
                                    copy(
                                        song = LinkData.Device(
                                            path = uri.toString()
                                        )
                                    )
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
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
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
                                                    imageVector = if (isSelected.value) EditIco else AddIco,
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
                                                playing = !playing
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
                                                    targetState = playing, label = "icon"
                                                ) { isSongPlaying ->
                                                    Icon(
                                                        imageVector = if (isSongPlaying) PauseIco else PlayArrowFilledIco,
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
                                    Button(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                            focusManager.clearFocus()
                                            isHorizontalVideoChangeDialogExpanded.value =
                                                true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
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
                                                    imageVector = if (state) EditIco else AddIco,
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
                                }
                            }
                        }
                    }

                    ElementType.PlaylistCard -> {
                        var isCardsChoiceOpen by rememberSaveable { mutableStateOf(false) }
                        if (isCardsChoiceOpen) {
                            CardChoice(
                                searchViewModel = searchViewModel,
                                isSingleChoice = false,
                                onDismiss = { isCardsChoiceOpen = false },
                                cardTypes = stateViewModel.state.playlistType.availableCardTypes(),
                                initCardsList = stateViewModel.state.cardsList,
                                onApply = {
                                    stateViewModel.update {
                                        copy(cardsList = it)
                                    }
                                }
                            )
                        }

                        var isChangePlayListTypeOpen by rememberSaveable { mutableStateOf(false) }
                        if (isChangePlayListTypeOpen) {
                            PlayListTypeChoice(
                                onDismiss = { isChangePlayListTypeOpen = false },
                                initType = stateViewModel.state.playlistType,
                                onApply = {
                                    if (
                                        (stateViewModel.state.playlistType == PlayListType.Anime && (it == PlayListType.Manga || it == PlayListType.Music)) ||
                                        (stateViewModel.state.playlistType == PlayListType.Manga && (it == PlayListType.Anime || it == PlayListType.Music)) ||
                                        (stateViewModel.state.playlistType == PlayListType.Music && it != PlayListType.Music) ||
                                        (stateViewModel.state.playlistType == PlayListType.AnimeNManga && it == PlayListType.Music)
                                    ) {
                                        stateViewModel.update {
                                            copy(cardsList = emptyList())
                                        }
                                    }
                                    stateViewModel.update { copy(playlistType = it) }
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Column(
                                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                            ) {
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        focusManager.clearFocus()
                                        isChangePlayListTypeOpen = true
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
                                            imageVector = EditIco,
                                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                            contentDescription = null
                                        )
                                        Text(
                                            text = stringResource(R.string.changePlayListType),
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        focusManager.clearFocus()
                                        isCardsChoiceOpen = true
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
                                            imageVector = EditIco,
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
                        }
                    }

                    ElementType.ArtistCard -> {
                        var isChangeArtistTypeOpen by rememberSaveable { mutableStateOf(false) }
                        if (isChangeArtistTypeOpen) {
                            ArtistTypeChoice(
                                onDismiss = { isChangeArtistTypeOpen = false },
                                initType = stateViewModel.state.artistType ?: ArtistType.Music,
                                onApply = {
                                    stateViewModel.update { copy(artistType = it) }
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Column(
                                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                            ) {
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        focusManager.clearFocus()
                                        isChangeArtistTypeOpen = true
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
                                            imageVector = EditIco,
                                            modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                            contentDescription = null
                                        )
                                        Text(
                                            text = stringResource(R.string.changeArtistType),
                                            style = MaterialTheme.typography.labelLarge,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    ElementType.AlbumCard -> {
                        var isCardsChoiceOpen by rememberSaveable { mutableStateOf(false) }
                        if (isCardsChoiceOpen) {
                            CardChoice(
                                searchViewModel = searchViewModel,
                                isSingleChoice = false,
                                onDismiss = { isCardsChoiceOpen = false },
                                cardTypes = listOf(ElementType.MusicCard),
                                initCardsList = stateViewModel.state.cardsList,
                                onApply = {
                                    stateViewModel.update {
                                        copy(cardsList = it)
                                    }
                                }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ) {
                            Column(
                                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                            ) {
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        focusManager.clearFocus()
                                        isCardsChoiceOpen = true
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
                                            imageVector = EditIco,
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
                        }
                    }

                    ElementType.Carousel -> {}
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = topInset + MaterialTheme.spacing.screenHorizontal,
                    start = MaterialTheme.spacing.screenHorizontal + leftInset
                )
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
                    imageVector = CloseIco,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                )
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
                    if (stateViewModel.state.cardType == ElementType.AnimeCard) {
                        PopupMenuItem(
                            text = stringResource(R.string.importFromAniLiberty),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                isExtraButtonsMenuOpened.value = false
                            },
                            icon = {
                                Icon(
                                    imageVector = DownloadIco2,
                                    contentDescription = null,
                                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                                )
                            }
                        )
                    } else {
                        PopupMenuItem(
                            text = stringResource(R.string.nothingIsHere),
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                            onClick = {},
                            icon = {}
                        )
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
                            stateViewModel.state.toObjectData(),
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
                        if (!isEditMode) {
                            Icon(
                                imageVector = AddIco,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                contentDescription = null
                            )
                        }

                        Text(
                            text = stringResource(if (!isEditMode) R.string.addCard else R.string.save),
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
                            copy(episodesList = newEpisodes.map {
                                it.toEpisodeInfo(
                                    newEpisodes.indexOf(
                                        it
                                    )
                                )
                            })
                        }
                    }
                    currentPendingEditEpisodesKey.value = ""
                }

                currentPendingEditChaptersKey.value -> {
                    val newData = data as? PageWithSearchSaveOutput
                    if (newData != null) {
                        val newChapters = newData.items.filterIsInstance<ChapterInfo>().toList()

                        stateViewModel.update {
                            copy(chaptersList = newChapters.map {
                                it.toChapterInfo(
                                    newChapters.indexOf(
                                        it
                                    )
                                )
                            })
                        }
                    }
                    currentPendingEditChaptersKey.value = ""
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayListTypeChoice(
    onDismiss: () -> Unit,
    initType: PlayListType,
    onApply: (PlayListType) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val coroutineScope = rememberCoroutineScope()

    val animateAndDismiss: () -> Unit = {
        bottomSheetAnimateAndDismiss(
            coroutineScope = coroutineScope,
            sheetState = sheetState,
            onDismiss = onDismiss
        )
    }

    val playListTypes by remember { mutableStateOf(PlayListType.entries.toList()) }

    var selectedType by remember { mutableStateOf(initType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing.large + MaterialTheme.dimens.minButtonHeight),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                items(
                    count = playListTypes.size,
                    key = { it.toString() },
                    contentType = { "PlayListTypeChoiceItem" }
                ) { index ->
                    val itemText = stringResource(playListTypes[index].displayNameId)
                    val isSelected = selectedType == playListTypes[index]

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                selectedType = playListTypes[index]
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.medium)
                        ) {
                            Text(
                                text = itemText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        focusManager.clearFocus()
                        onApply(selectedType)
                        animateAndDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.apply),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistTypeChoice(
    onDismiss: () -> Unit,
    initType: ArtistType,
    onApply: (ArtistType) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val coroutineScope = rememberCoroutineScope()

    val animateAndDismiss: () -> Unit = {
        bottomSheetAnimateAndDismiss(
            coroutineScope = coroutineScope,
            sheetState = sheetState,
            onDismiss = onDismiss
        )
    }

    val artistTypes by remember { mutableStateOf(ArtistType.entries.toList()) }

    var selectedType by remember { mutableStateOf(initType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Box {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing.large + MaterialTheme.dimens.minButtonHeight),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                items(
                    count = artistTypes.size,
                    key = { it.toString() },
                    contentType = { "ArtistTypeChoiceItem" }
                ) { index ->
                    val itemText = stringResource(artistTypes[index].displayNameId)
                    val isSelected = selectedType == artistTypes[index]

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                selectedType = artistTypes[index]
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.medium)
                        ) {
                            Text(
                                text = itemText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        focusManager.clearFocus()
                        onApply(selectedType)
                        animateAndDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.apply),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistSearch(
    artistsFlow: List<String>,
    onArtistChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        artistsFlow.forEach { artist ->
            SuggestionChip(
                label = {
                    Text(
                        text = artist,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = {
                    onArtistChange(artist)
                },
                shape = MaterialTheme.shapes.small,
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            )
        }
    }
}
