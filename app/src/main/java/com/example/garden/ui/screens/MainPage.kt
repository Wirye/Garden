package com.example.garden.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.database.CarouselType
import com.example.garden.database.ElementType
import com.example.garden.database.ImageData
import com.example.garden.database.LayoutType
import com.example.garden.database.ObjectData
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.ui.components.AppAsyncImage
import com.example.garden.ui.components.CarouselFactory
import com.example.garden.ui.components.DropDownMenuWithBlur
import com.example.garden.ui.components.FullScreenBlobs
import com.example.garden.ui.components.MainPageTopBar
import com.example.garden.ui.components.PopupMenuItem
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.components.icons.ChevronForwardText
import com.example.garden.ui.components.icons.DeleteIco
import com.example.garden.ui.components.icons.EditIco
import com.example.garden.ui.components.icons.MoreVertIco
import com.example.garden.ui.components.icons.SettingsIco
import com.example.garden.ui.components.rememberSmartCollapsingTopBarState
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowInfo
import com.example.garden.ui.theme.windowSizeClass
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize
import com.example.garden.ui.utils.getAspectRatio
import com.example.garden.ui.utils.toDp
import com.example.garden.viewmodel.MainViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs
import kotlin.math.round
import androidx.compose.runtime.collectAsState
import com.example.garden.database.PlayListType
import com.example.garden.viewmodel.CarouselState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPage(
    viewModel: MainViewModel,
    layer: Layer.MainPage,
    isTopLayer: Boolean,
    topBarHeight: Dp,
    bottomBarHeight: Dp,
    openSettings: () -> Unit,
    openCreateCardPage: (Long, CarouselType) -> Unit,
    openCreateCarouselPage: () -> Unit,
    openEditCarouselPage: (ObjectData.Carousel) -> Unit,
    openEditCardPage: (ObjectData.Card, Long, CarouselType) -> Unit,
    openCarouselChildsEdit: (Long) -> Unit,
    deleteCard: (Long) -> Unit,
    deleteCarousel: (Long) -> Unit
) {
    var visibleCardPos by rememberSaveable(layer.id) {
        mutableIntStateOf(layer.firstElementPosition)
    }

    val topBarHeightPx = with(LocalDensity.current) { topBarHeight.toPx().toInt() }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = layer.firstElementPosition,
        initialFirstVisibleItemScrollOffset = -topBarHeightPx
    )

    LaunchedEffect(listState, layer.id) {
        snapshotFlow {
            val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()
            firstVisibleItem?.index
        }.distinctUntilChanged()
            .filterNotNull()
            .collect { holderIndex ->
                visibleCardPos = holderIndex
                layer.firstElementPosition = holderIndex
            }
    }

    val density = LocalDensity.current
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val topInset = with(density) { topInsetPx.toDp() }

    val topBarState =
        rememberSmartCollapsingTopBarState(topBarHeight, minBarOffset = -topInset, listState)

    LaunchedEffect(isTopLayer) {
        topBarState.resetBarOffset()
    }

    val pagingItems: LazyPagingItems<ObjectWithChilds2> =
        viewModel.getPagePagingObjects(layer.page).collectAsLazyPagingItems()

    val arrangementSpacing = MaterialTheme.spacing.extraLarge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .blockGestures(),
        contentAlignment = Alignment.TopCenter
    ) {
        val screenHeight = with(density) { MaterialTheme.windowInfo.heightDp.toPx() }
        val heightPx = with(density) { topBarHeight.toPx() }.coerceAtLeast(1f)
        val translationY = {
            val fraction = (topBarState.firstElementScrollOffset / heightPx).coerceIn(0f, 1f)
            -fraction * screenHeight
        }

        val isStrokeVisible by remember(topBarHeight) {
            derivedStateOf {
                val fraction =
                    (abs(topBarState.firstElementScrollOffset) / heightPx).coerceIn(0f, 1f)
                fraction > 0.2f
            }
        }

        FullScreenBlobs(translationY = translationY())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithContent {
                    drawContent()
                    val topBarVisibleHeight =
                        (heightPx + topBarState.barOffsetPx).coerceAtLeast(0f)
                    drawRect(
                        color = Color.Black,
                        size = Size(size.width, topBarVisibleHeight),
                        blendMode = BlendMode.DstOut
                    )
                }
        ) {
            if (pagingItems.itemCount != 0) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(topBarState.nestedScrollConnection),
                    verticalArrangement = Arrangement.spacedBy(arrangementSpacing),
                ) {
                    item(key = "topBarPadding") {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(topBarHeight)
                        )
                    }

                    items(
                        count = pagingItems.itemCount,
                        key = pagingItems.itemKey { if (it.parent.id == 0L) "temp_${it.hashCode()}" else it.parent.id },
                        contentType = { "carousel" }
                    ) { index ->
                        val item = pagingItems[index]

                        if (item != null) {
                            val carouselState = viewModel.carouselStates.collectAsState().value[item.parent.id] ?: CarouselState.Success

                            val cards = if (carouselState is CarouselState.Loading && item.childs.isEmpty()) {
                                List(15) { index ->
                                    when (item.parent.carouselType) {
                                        CarouselType.Anime, CarouselType.AnimeNManga -> ObjectData.Card.Anime(
                                            id = 0L, position = index, name = "", image = ImageData.Url("")
                                        )
                                        CarouselType.Manga -> ObjectData.Card.Manga(
                                            id = 0L, position = index, name = "", image = ImageData.Url("")
                                        )
                                        CarouselType.Music, CarouselType.PlaylistNMusic -> ObjectData.Card.Music(
                                            id = 0L, position = index, name = "", image = ImageData.Url("")
                                        )
                                        CarouselType.Playlist -> ObjectData.Card.Playlist(
                                            id = 0L, position = index, name = "", image = ImageData.Url(""), playListType = PlayListType.Music
                                        )
                                    }
                                }
                            } else {
                                item.childs
                            }

                            if (carouselState is CarouselState.Success || carouselState is CarouselState.Loading) {
                                Carousel(
                                    lineWidth = MaterialTheme.windowInfo.widthDp,
                                    layer = layer,
                                    carouselData = item.parent,
                                    cards = cards,
                                    onAddCard = openCreateCardPage,
                                    onEditCarousel = { openCarouselChildsEdit(item.parent.id) },
                                    onEditCarouselSettings = { openEditCarouselPage(item.parent) },
                                    onClickCard = {},
                                    onWatchAllClick = {},
                                    onEditCard = { openEditCardPage(it, item.parent.id, item.parent.carouselType) },
                                    onDeleteCard = deleteCard,
                                    onDeleteCarousel = deleteCarousel
                                )
                            } else if (carouselState is CarouselState.Error) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = MaterialTheme.spacing.large),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = carouselState.message,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    item(key = "bottomBarPadding") {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(arrangementSpacing + bottomBarHeight)
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.nothingIsHere),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        MainPageTopBar(
            offsetPx = { topBarState.barOffsetPx },
            modifier = Modifier
                .align(Alignment.TopCenter),
            isStrokeVisible = isStrokeVisible,
            onSearch = {},
            onSettings = openSettings,
            onAddCarousel = {
                openCreateCarouselPage()
            },
            onEdit = {}
        )
    }
}

@Composable
fun CarouselPreview(
    lineWidth: Dp,
    info: Layer.CreateCarouselPage
) {
    val layer = info.localLayer

    val childs = List(20) { index ->
        when (info.carouselType) {
            CarouselType.Anime, CarouselType.AnimeNManga -> {
                ObjectData.Card.Anime(
                    id = index.toLong() + 1L,
                    position = index,
                    name = index.toString(),
                    image = ImageData.Url("")
                )
            }
            CarouselType.Manga -> {
                ObjectData.Card.Manga(
                    id = index.toLong() + 1L,
                    position = index,
                    name = index.toString(),
                    image = ImageData.Url("")
                )
            }
            CarouselType.Music, CarouselType.PlaylistNMusic -> {
                ObjectData.Card.Music(
                    id = index.toLong() + 1L,
                    position = index,
                    name = index.toString(),
                    image = ImageData.Url("")
                )
            }
            else -> {
                ObjectData.Card.Anime(
                    id = index.toLong() + 1L,
                    position = index,
                    name = index.toString(),
                    image = ImageData.Url("")
                )
            }
        }
    }

    val carouselData = remember(info) {
        mutableStateOf(
            ObjectData.Carousel(
                position = 0,
                name = info.name,
                childsCornerRadius = info.childsCornerRadius,
                childsShowName = info.childsShowName,
                childsSize = info.childsSize,
                childsShowAuthor = info.childsShowAuthor,
                childsNamePosition = info.childsNamePosition,
                childsShowAlreadyWatchedLine = info.childsShowAlreadyWatchedLine,
                carouselType = info.carouselType,
                carouselCollectionType = info.carouselCollectionType,
                layoutType = info.layoutType,
                maxLines = info.maxLines,
                maxObjectsInOneLineForAdaptiveSize = info.maxObjectsInOneLineForAdaptiveSize,
                maxLinesForAdaptiveSize = info.maxLinesForAdaptiveSize,
                objectsInOneLine = info.objectsInOneLine,
                adaptiveGridSize = info.adaptiveGridSize,
                showIco = info.showIco,
                ico = info.ico,
                dovodchik = info.dovodchik,
                showDovodchikDots = info.showDovodchikDots,
                id = 21L
            )
        )
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item("carousel") {
            Carousel(
                lineWidth = lineWidth,
                layer = layer,
                carouselData = carouselData.value,
                cards = childs,
                onAddCard = { _, _ -> },
                onEditCarousel = {},
                onClickCard = {},
                onWatchAllClick = {},
                onEditCarouselSettings = {},
                onEditCard = {},
                onDeleteCard = { _ -> },
                onDeleteCarousel = { _ -> }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Carousel(
    lineWidth: Dp,
    layer: Layer.MainPage,
    carouselData: ObjectData.Carousel,
    cards: List<ObjectData.Card>,
    onAddCard: (Long, CarouselType) -> Unit,
    onEditCarousel: () -> Unit,
    onEditCarouselSettings: () -> Unit,
    onClickCard: (ObjectData) -> Unit,
    onWatchAllClick: (() -> Unit)? = null,
    onEditCard: (ObjectData.Card) -> Unit,
    onDeleteCard: (Long) -> Unit,
    onDeleteCarousel: (Long) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val density = LocalDensity.current
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)

    val rightInset = with(density) { rightInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.spacing.screenHorizontal + leftInset,
                        end = MaterialTheme.spacing.screenHorizontal + rightInset
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    if (carouselData.showIco && carouselData.ico != null) {
                        AppAsyncImage(
                            imageData = carouselData.ico,
                            contentDescription = null,
                            modifier = Modifier
                                .size(MaterialTheme.dimens.minButtonHeight)
                                .clip(MaterialTheme.shapes.extraSmall),
                        )

                        Spacer(
                            modifier = Modifier.size(MaterialTheme.spacing.small)
                        )
                    }

                    val carouselName = carouselData.name.ifEmpty {
                        stringResource(
                            id = R.string.withoutName
                        )
                    }

                    Text(
                        text = carouselName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )

                    val isWatchAllButtonExist = onWatchAllClick != null
                    if (isWatchAllButtonExist) {
                        Box(
                            modifier = Modifier
                                .defaultMinSize(
                                    minWidth = MaterialTheme.dimens.minButtonHeight,
                                    minHeight = MaterialTheme.dimens.minButtonHeight
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    onWatchAllClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = ChevronForwardText,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                                contentDescription = null
                            )
                        }
                    }
                }

                val isExpanded = remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            isExpanded.value = !isExpanded.value
                        },
                    ) {
                        Icon(
                            imageVector = MoreVertIco,
                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }

                    DropDownMenuWithBlur(
                        expanded = { isExpanded.value },
                        hazeState = LocalHazeLayers.current.mainScreen,
                        onDismissRequest = { isExpanded.value = false }
                    ) {
                        PopupMenuItem(
                            text = stringResource(R.string.edit),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onEditCarousel()
                                isExpanded.value = false
                            }
                        ) {
                            Icon(
                                imageVector = EditIco,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }

                        PopupMenuItem(
                            text = stringResource(R.string.addCard),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onAddCard(
                                    carouselData.id,
                                    carouselData.carouselType
                                )
                                isExpanded.value = false
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
                            text = stringResource(R.string.setting),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onEditCarouselSettings()
                                isExpanded.value = false
                            }
                        ) {
                            Icon(
                                imageVector = SettingsIco,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = null
                            )
                        }

                        PopupMenuItem(
                            text = stringResource(R.string.delete),
                            textColor = LocalCustomColors.current.closeButton,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onDeleteCarousel(carouselData.id)
                                isExpanded.value = false
                            }
                        ) {
                            Icon(
                                imageVector = DeleteIco,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                tint = LocalCustomColors.current.closeButton,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            val childsSize = carouselData.childsSize
            if (cards.isNotEmpty()) {
                val margin = MaterialTheme.spacing.medium

                val cardElType = when (cards[0]) {
                    is ObjectData.Card.Anime -> ElementType.AnimeCard
                    is ObjectData.Card.Manga -> ElementType.MangaCard
                    is ObjectData.Card.Music -> ElementType.MusicCard
                    is ObjectData.Card.Playlist -> ElementType.PlaylistCard
                }

                val cardAspRatio = cardElType.getAspectRatio()

                val paddingHorizontal = MaterialTheme.spacing.screenHorizontal

                val cardWidth = when (carouselData.layoutType) {
                    LayoutType.CAROUSEL_FROM_GRID -> {
                        lineWidth.value.dp - rightInset - leftInset - paddingHorizontal * 2
                    }

                    LayoutType.CAROUSEL_FROM_FLAT_GRID -> {
                        (lineWidth.value.dp - paddingHorizontal * 2 - rightInset - leftInset - MaterialTheme.spacing.extraLarge).coerceAtMost(
                            600.dp
                        )
                    }

                    else -> {
                        childsSize.toDp(
                            cardType = cardElType, maxCardWidth = lineWidth -
                                    (MaterialTheme.spacing.screenHorizontal * 2),
                            windowWidthSizeClass = MaterialTheme.windowSizeClass.widthSizeClass
                        ).width
                    }
                }

                val oneCardWidth = childsSize.toDp(
                    cardType = cardElType, maxCardWidth = lineWidth -
                            (MaterialTheme.spacing.screenHorizontal * 2),
                    windowWidthSizeClass = MaterialTheme.windowSizeClass.widthSizeClass
                ).width


                val cardsInOneLine = round(lineWidth / oneCardWidth).toInt()

                val res = if (carouselData.adaptiveGridSize) calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(
                    parent = carouselData,
                    objectsInOneLine = cardsInOneLine
                ) else Pair(carouselData.objectsInOneLine ?: cardsInOneLine.coerceAtLeast(1), carouselData.maxLines)

                val newCarouselData = carouselData.copy(
                    objectsInOneLine = res.first,
                    maxLines = res.second
                )

                CarouselFactory(
                    layer = layer,
                    carousel = newCarouselData,
                    childs = cards,
                    cardWidth = cardWidth,
                    cardAspectRatio = cardAspRatio,
                    paddingStart = leftInset + paddingHorizontal,
                    paddingEnd = rightInset + paddingHorizontal,
                    marginBetweenElements = margin,
                    onEditCard = onEditCard,
                    onClickCard = onClickCard,
                    onDeleteCard = onDeleteCard
                )
            }
            else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.nothingIsHere),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
