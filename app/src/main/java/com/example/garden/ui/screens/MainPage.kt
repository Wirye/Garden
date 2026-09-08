package com.example.garden.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListPrefetchStrategy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.garden.Layer
import com.example.garden.R
import com.example.garden.database.CarouselType
import com.example.garden.database.ElementType
import com.example.garden.database.LayoutType
import com.example.garden.database.ObjectData2
import com.example.garden.database.PageType
import com.example.garden.database.SizeType
import com.example.garden.ui.components.AsyncImageWithAddPlaceholder
import com.example.garden.ui.components.Card
import com.example.garden.ui.components.DropDownMenuWithBlur
import com.example.garden.ui.components.FullScreenBlobs
import com.example.garden.ui.components.GridOfCards
import com.example.garden.ui.components.MainPageTopBar
import com.example.garden.ui.components.PopupMenuItem
import com.example.garden.ui.components.rememberSmartCollapsingTopBarState
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowInfo
import com.example.garden.ui.theme.windowSizeClass
import com.example.garden.ui.utils.availableCardTypes
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize
import com.example.garden.ui.utils.cardScaleCalcForGrid
import com.example.garden.ui.utils.dataForModel
import com.example.garden.ui.utils.getAspectRatio
import com.example.garden.ui.utils.toDp
import com.example.garden.ui.utils.uploadLayoutTypeToCarouselChilds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPage(
    layer: Layer.MainPage,
    carouselsList: List<ObjectData2>,
    isTopLayer: Boolean,
    topBarHeight: Dp,
    bottomBarHeight: Dp,
    openCreateCardPage: (Long, CarouselType) -> Unit,
    openCreateCarouselPage: () -> Unit,
    openEditCarouselPage: (ObjectData2) -> Unit
) {
    var visibleCardPos by rememberSaveable(layer.id) {
        mutableIntStateOf(layer.firstElementPosition)
    }

    val topBarHeightPx = with(LocalDensity.current) { topBarHeight.toPx().toInt() }

    val prefetchStrategy = remember {
        LazyListPrefetchStrategy(nestedPrefetchItemCount = 3)
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = layer.firstElementPosition,
        initialFirstVisibleItemScrollOffset = -topBarHeightPx,
        prefetchStrategy = prefetchStrategy
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
                val fraction = (topBarState.firstElementScrollOffset / heightPx).coerceIn(0f, 1f)
                fraction == 1f
            }
        }

        FullScreenBlobs(translationY = translationY)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithContent {
                    drawContent()
                    val topBarVisibleHeight =
                        { (heightPx + topBarState.barOffsetPx).coerceAtLeast(0f) }
                    drawRect(
                        color = Color.Black,
                        size = Size(size.width, topBarVisibleHeight()),
                        blendMode = BlendMode.DstOut
                    )
                }
        ) {
            if (carouselsList.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(topBarState.nestedScrollConnection),
                    verticalArrangement = Arrangement.spacedBy(arrangementSpacing),
                ) {
                    item("topBarPadding") {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(topBarHeight)
                        )
                    }

                    items(
                        items = carouselsList,
                        key = { carousel -> carousel.id },
                        contentType = { "carousel" }
                    ) { carousel ->
                        Box {
                            Carousel(
                                layer = layer,
                                carouselData = carousel,
                                onAddCard = { parentId, carouselType ->
                                    openCreateCardPage(
                                        parentId,
                                        carouselType
                                    )
                                },
                                onEditCarousel = {},
                                onClickCard = {},
                                onWatchAllClick = {},
                                onEditCarouselSettings = {
                                    openEditCarouselPage(it)
                                },
                                modifier = Modifier.offset(
                                    y = -arrangementSpacing
                                )
                            )
                        }
                    }

                    item("bottomBarPadding") {
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
            offsetPx = topBarState.barOffsetPx,
            modifier = Modifier
                .align(Alignment.TopCenter),
            isStrokeVisible = isStrokeVisible,
            onSearch = {},
            onSettings = {},
            onAddCarousel = {
                openCreateCarouselPage()
            },
            onEdit = {}
        )
    }
}

@Composable
fun CarouselPreview(
    info: Layer.CreateCarouselPage
) {
    val layer = info.localLayer

    val childs = remember(info) {
        mutableStateListOf<ObjectData2>().apply {
            repeat(20) {
                add(
                    ObjectData2(
                        id = it.toLong(),
                        page = PageType.Home,
                        position = it,
                        name = "${it + 1}",
                        author = null,
                        elementType = info.carouselType.availableCardTypes().firstOrNull()
                            ?: ElementType.AnimeCard,
                        childs = emptyList(),
                        alreadyWatched = 1L,
                        length = 2L,
                        layoutType = if (info.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID) LayoutType.FLAT_GRID_ITEM else LayoutType.DEFAULT
                    )
                )
            }
        }
    }

    val carouselData = remember(info) {
        mutableStateOf(
            ObjectData2(
                page = PageType.Home,
                position = 0,
                name = info.name,
                childsCornerRadius = info.childsCornerRadius,
                childsShowName = info.childsShowName,
                childsSize = info.childsSize,
                childsShowAuthor = info.childsShowAuthor,
                childsNamePosition = info.childsNamePosition,
                childsShowAlreadyWatchedLine = info.childsShowAlreadyWatchedLine,
                alreadyWatched = 0L,
                length = 0L,
                elementType = ElementType.Carousel,
                carouselType = info.carouselType,
                carouselCollectionType = info.carouselCollectionType,
                childs = childs,
                layoutType = info.layoutType,
                maxLines = info.maxLines,
                maxObjectsInOneLineForAdaptiveSize = info.maxObjectsInOneLineForAdaptiveSize,
                maxLinesForAdaptiveSize = info.maxLinesForAdaptiveSize,
                objectsInOneLine = info.objectsInOneLine,
                adaptiveGridSize = info.adaptiveGridSize,
                showIco = info.showIco,
                image = info.ico,
                dovodchik = info.dovodchik,
                showDovodchikDots = info.showDovodchikDots
            )
        )
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item("carousel") {
            Carousel(
                layer = layer,
                carouselData = carouselData.value,
                onAddCard = { _, _ -> },
                onEditCarousel = {},
                onClickCard = {},
                onWatchAllClick = {},
                onEditCarouselSettings = {}
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Carousel(
    modifier: Modifier = Modifier,
    layer: Layer.MainPage,
    carouselData: ObjectData2,
    onAddCard: (Long, CarouselType) -> Unit,
    onEditCarousel: () -> Unit,
    onEditCarouselSettings: (ObjectData2) -> Unit,
    onClickCard: (ObjectData2) -> Unit,
    onWatchAllClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    val density = LocalDensity.current
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)

    val rightInset = with(density) { rightInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }

    val isAttached = remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                isAttached.value = coords.isAttached
            }
    ) {
        val columnWidth = maxWidth
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = MaterialTheme.spacing.screenHorizontal + leftInset,
                            end = MaterialTheme.spacing.screenHorizontal + rightInset
                        )
                ) {
                    val (titleContainerRef, extraButtonRef) = createRefs()
                    val isExpanded = remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier.constrainAs(extraButtonRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                    ) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                isExpanded.value = !isExpanded.value
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.more_vert_ico),
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
                                    painter = painterResource(R.drawable.edit_ico),
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
                                        carouselData.carouselType ?: CarouselType.Anime
                                    )
                                    isExpanded.value = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_ico),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null
                                )
                            }
                            PopupMenuItem(
                                text = stringResource(R.string.setting),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    onEditCarouselSettings(carouselData)
                                    isExpanded.value = false
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.settings_ico),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        modifier = Modifier.constrainAs(titleContainerRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(extraButtonRef.start)
                            width = Dimension.fillToConstraints
                        }
                    ) {
                        if (carouselData.showIco && carouselData.image != null) {
                            AsyncImageWithAddPlaceholder(
                                modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight),
                                model = carouselData.image?.dataForModel(),
                                shape = MaterialTheme.shapes.extraSmall
                            )

                            Spacer(
                                modifier = Modifier.size(MaterialTheme.spacing.small)
                            )
                        }

                        val carouselName = (carouselData.name
                            ?: stringResource(id = R.string.withoutName)).ifEmpty {
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
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.chevron_forward_text),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                val cards =
                    uploadLayoutTypeToCarouselChilds(
                        carouselData.childs,
                        carouselData,
                        columnWidth
                    )

                var visibleCardPos = layer.scrollPositionCarousels[carouselData.id] ?: 0

                val prefetchStrategy = remember {
                    LazyListPrefetchStrategy(nestedPrefetchItemCount = 3)
                }

                val holderOnlyIndexSaver = Saver<LazyListState, Pair<Int, Int>>(
                    save = { Pair(toFirstVisible(cards, it.firstVisibleItemIndex), 0) },
                    restore = { restoredIndex ->
                        LazyListState(
                            firstVisibleItemIndex = fromFirstVisible(
                                cards,
                                restoredIndex.component1()
                            ),
                            firstVisibleItemScrollOffset = restoredIndex.component2(),
                            prefetchStrategy = prefetchStrategy
                        )
                    }
                )

                val currentHolderIndex: (Int) -> Int = {
                    fromFirstVisible(cards, it)
                }

                val listState = rememberSaveable(
                    carouselData.id,
                    saver = holderOnlyIndexSaver
                ) {
                    LazyListState(
                        firstVisibleItemIndex = currentHolderIndex(visibleCardPos),
                        firstVisibleItemScrollOffset = 0,
                    )
                }

                LaunchedEffect(Unit, listState, carouselData.id) {
                    snapshotFlow { listState.isScrollInProgress }
                        .collect { isScrolling ->
                            if (!isScrolling && listState.layoutInfo.totalItemsCount > 0) {
                                val holderIndex = listState.firstVisibleItemIndex
                                val cardPos = toFirstVisible(cards, holderIndex)
                                if (cardPos != visibleCardPos) {
                                    visibleCardPos = cardPos
                                    layer.scrollPositionCarousels[carouselData.id] =
                                        visibleCardPos
                                }
                            }
                        }
                }

                val childsSize = carouselData.childsSize
                if (cards.isNotEmpty() && carouselData.layoutType != LayoutType.CAROUSEL_GRID && childsSize != null) {
                    val margin = when (carouselData.layoutType) {
                        LayoutType.CAROUSEL_GRID, LayoutType.CAROUSEL_FROM_FLAT_GRID,
                        LayoutType.CAROUSEL_FROM_GRID -> 0.dp

                        else -> MaterialTheme.spacing.medium
                    }

                    val gridMode =
                        carouselData.layoutType == LayoutType.CAROUSEL_FROM_GRID || carouselData.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID

                    val cardElType = cards.first().elementType
                    val cardWidth = when (carouselData.layoutType) {
                        LayoutType.CAROUSEL_FROM_GRID, LayoutType.CAROUSEL_GRID -> {
                            columnWidth.value.dp
                        }

                        LayoutType.CAROUSEL_FROM_FLAT_GRID -> {
                            (columnWidth.value.dp - MaterialTheme.spacing.screenHorizontal - MaterialTheme.spacing.extraLarge).coerceAtMost(
                                600.dp
                            )
                        }

                        else -> {
                            childsSize.toDp(
                                cardType = cardElType, maxCardWidth = columnWidth -
                                        (MaterialTheme.spacing.screenHorizontal * 2),
                                windowWidthSizeClass = MaterialTheme.windowSizeClass.widthSizeClass
                            ).width
                        }
                    }

                    val cardAspRatio = cardElType.getAspectRatio()

                    val paddingHorizontal = MaterialTheme.spacing.screenHorizontal

                    val padding =
                        if (carouselData.layoutType == LayoutType.CAROUSEL_GRID ||
                            carouselData.layoutType == LayoutType.CAROUSEL_FROM_GRID ||
                            carouselData.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID
                        ) 0.dp
                        else paddingHorizontal

                    val paddingStart =
                        padding + if (carouselData.layoutType == LayoutType.CAROUSEL_FROM_GRID ||
                            carouselData.layoutType == LayoutType.CAROUSEL_GRID ||
                            carouselData.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID
                        ) {
                            0.dp
                        } else leftInset

                    val paddingEnd =
                        padding + if (carouselData.layoutType == LayoutType.CAROUSEL_FROM_GRID ||
                            carouselData.layoutType == LayoutType.CAROUSEL_GRID ||
                            carouselData.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID
                        ) {
                            0.dp
                        } else rightInset

                    val dots = remember(cards, columnWidth) {
                        calculateAmountOfDots(
                            itemsCount = cards.size,
                            paddingStart = paddingStart,
                            paddingEnd = paddingEnd,
                            marginBetweenElementsHorizontal = margin,
                            lineWidth = columnWidth,
                            cardWidth = cardWidth
                        )
                    }

                    val carouselState = rememberCarouselState(
                        dotList = dots,
                        listState = listState,
                    )

                    val flingBehavior = rememberCarouselSnapFlingBehavior(
                        carouselState = carouselState
                    )
                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(Unit, carouselData.id) {
                        snapshotFlow { listState.layoutInfo.totalItemsCount }.first { it > 0 }

                        val targetIndex = currentHolderIndex(visibleCardPos)
                        if (listState.firstVisibleItemIndex != targetIndex) {
                            listState.scrollToItem(targetIndex)
                            val activeDot = carouselState.calculateTargetIndexForFling(0f)
                            if (carouselState.activeDot != activeDot && carouselData.dovodchik) {
                                carouselState.scrollToDot(activeDot, false)
                            }
                        }
                    }

                    LaunchedEffect(Unit, carouselState) {
                        val activeDot = carouselState.calculateTargetIndexForFling(0f)
                        carouselState.scrollToDot(activeDot, false)
                    }

                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(start = paddingStart, end = paddingEnd),
                        horizontalArrangement = Arrangement.spacedBy(margin),
                        flingBehavior = if (carouselData.dovodchik) flingBehavior else ScrollableDefaults.flingBehavior(),
                    ) {
                        items(
                            items = cards,
                            key = { card -> card.id }
                        ) { card ->
                            if (gridMode) {
                                val allCardsAmount = remember {
                                    mutableIntStateOf(0).apply {
                                        cards.forEach {
                                            intValue += it.childs.size
                                        }
                                    }
                                }

                                GridOfCards(
                                    allCardsAmount = allCardsAmount.intValue,
                                    gridInfo = card,
                                    maxLines = card.maxLines ?: Int.MAX_VALUE,
                                    maxObjectsInOneLine = card.objectsInOneLine ?: 1,
                                    lineWidth = if (card.layoutType == LayoutType.CARD_FLAT_GRID) cardWidth else columnWidth,
                                    paddingStart = leftInset + if (carouselData.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID) MaterialTheme.spacing.screenHorizontal / 2 else MaterialTheme.spacing.screenHorizontal,
                                    paddingEnd = rightInset + MaterialTheme.spacing.screenHorizontal,
                                    marginBetweenElements = when (carouselData.layoutType) {
                                        LayoutType.CAROUSEL_FROM_FLAT_GRID -> 0.dp
                                        else -> MaterialTheme.spacing.marginBetweenElementsInGrid
                                    },
                                    onCardClick = { onClickCard(it) }
                                )
                            } else {
                                Card(
                                    width = cardWidth,
                                    aspectRatio = cardAspRatio,
                                    showAlreadyWatchedLine = carouselData.childsShowAlreadyWatchedLine,
                                    alreadyWatched = card.alreadyWatched,
                                    length = card.length,
                                    image = card.image,
                                    showName = carouselData.childsShowName,
                                    name = card.name,
                                    namePosition = carouselData.childsNamePosition ?: 1,
                                    showAuthor = carouselData.childsShowAuthor,
                                    author = card.author,
                                    cornerRadius = carouselData.childsCornerRadius
                                        ?: SizeType.ESMALL,
                                    layoutType = card.layoutType,
                                    onClick = { onClickCard(card) }
                                )
                            }
                        }
                    }
                    if (cards.isNotEmpty() && carouselData.dovodchik && carouselData.showDovodchikDots) {
                        CarouselIndicators(
                            currentPage = carouselState.activeDot,
                            onPageClick = {
                                coroutineScope.launch {
                                    carouselState.scrollToDot(it)
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            lineWidth = columnWidth,
                            snapPointsDp = dots
                        )
                    }
                } else {
                    if (cards.isNotEmpty() && childsSize != null) {
                        val cardType = cards.first().elementType
                        val width = childsSize.toDp(
                            cardType,
                            columnWidth,
                            MaterialTheme.windowSizeClass.widthSizeClass
                        ).width
                        val margin = MaterialTheme.spacing.marginBetweenElementsInGrid
                        val amountOfCards =
                            cardScaleCalcForGrid(
                                width,
                                columnWidth,
                                margin,
                                null
                            ).second.toInt()
                        val res = calculateObjectsInOneLineAndMaxLinesForAdaptiveGridSize(
                            carouselData,
                            amountOfCards
                        )
                        val maxLines = res.second ?: Int.MAX_VALUE
                        val objectsInOneLine = res.first

                        val allCardsAmount = remember { mutableIntStateOf(0) }
                        cards.forEach {
                            allCardsAmount.intValue += it.childs.size
                        }

                        GridOfCards(
                            allCardsAmount = allCardsAmount.intValue,
                            gridInfo = carouselData,
                            maxLines = maxLines,
                            maxObjectsInOneLine = objectsInOneLine,
                            lineWidth = columnWidth,
                            paddingStart = leftInset + MaterialTheme.spacing.screenHorizontal,
                            paddingEnd = rightInset + MaterialTheme.spacing.screenHorizontal,
                            marginBetweenElements = MaterialTheme.spacing.marginBetweenElementsInGrid,
                            onCardClick = { onClickCard(it) }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.extraLarge)
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
    }
}


@Composable
private fun CarouselIndicators(
    currentPage: Int,
    onPageClick: (Int) -> Unit,
    snapPointsDp: List<ListDot>,
    lineWidth: Dp,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
) {
    val haptic = LocalHapticFeedback.current

    val totalCount = snapPointsDp.size
    if (totalCount <= 1) return

    val listState = rememberLazyListState()

    LaunchedEffect(currentPage) {
        val visibleItems = listState.layoutInfo.visibleItemsInfo
        if (visibleItems.isNotEmpty()) {
            val lastVisibleIndex = visibleItems.last().index
            val firstVisibleIndex = visibleItems.first().index

            if (currentPage >= lastVisibleIndex) {
                listState.animateScrollToItem(currentPage)
            } else if (currentPage < firstVisibleIndex) {
                listState.animateScrollToItem(currentPage)
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .width(lineWidth)
            .padding(vertical = MaterialTheme.spacing.extraSmall),
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.screenHorizontal),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.dotSpacing,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(totalCount) { index ->
            val isSelected = index == currentPage

            val width by animateDpAsState(
                targetValue = if (isSelected) MaterialTheme.dimens.activeDotWidth else MaterialTheme.dimens.dotSize,
                animationSpec = tween(durationMillis = 200),
                label = "dotWidth"
            )

            val color by animateColorAsState(
                targetValue = if (isSelected) activeColor else inactiveColor,
                animationSpec = tween(durationMillis = 200),
                label = "dotColor"
            )

            Box(
                modifier = Modifier
                    .size(width = width, height = MaterialTheme.dimens.dotSize)
                    .clip(CircleShape)
                    .background(color)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        onPageClick(index)
                    }
            )
        }
    }
}

private fun toFirstVisible(list: List<ObjectData2>, firstVisibleHolder: Int): Int {
    if (list.isEmpty()) return 0
    val safeHolderIndex = firstVisibleHolder.coerceIn(0, list.lastIndex)
    val firstVisibleItem = list[safeHolderIndex]

    return if (firstVisibleItem.layoutType == LayoutType.CARD_GRID || firstVisibleItem.layoutType == LayoutType.CARD_FLAT_GRID) {
        var objPos = 0
        for (i in 0 until safeHolderIndex) {
            val obj = list[i]
            objPos += obj.childs.size
            if (i == safeHolderIndex - 1) {
                objPos += 1
            }
        }
        objPos
    } else {
        safeHolderIndex
    }
}

private fun fromFirstVisible(list: List<ObjectData2>, firstVisible: Int): Int {
    if (list.isEmpty()) return 0

    val isGridPack =
        list.firstOrNull()?.layoutType == LayoutType.CARD_GRID || list.firstOrNull()?.layoutType == LayoutType.CARD_FLAT_GRID
    if (isGridPack) {
        var currentMaxPos = -1
        for (i in list.indices) {
            val obj = list[i]
            currentMaxPos += obj.childs.size
            if (firstVisible <= currentMaxPos) {
                return i
            }
        }
        return list.lastIndex
    } else {
        return firstVisible.coerceIn(0, list.lastIndex)
    }
}

private data class ListDot(
    val targetPos: Int,
    val offset: Dp
)

private fun calculateAmountOfDots(
    itemsCount: Int,
    cardWidth: Dp,
    marginBetweenElementsHorizontal: Dp,
    paddingStart: Dp,
    paddingEnd: Dp,
    lineWidth: Dp // Excluding indents, grid width will be lineWidth - (paddingStart + paddingEnd)
): List<ListDot> {
    if (itemsCount <= 0) return emptyList()

// The first point always points to the 0th element with an offset of -paddingHorizontal
    val firstDotOffset = -paddingStart
    val initialDot = ListDot(targetPos = 0, offset = firstDotOffset)

    if (itemsCount == 1) return listOf(initialDot)

// Calculating the total content width and maximum scroll
    val totalContentWidth = (paddingStart + paddingEnd) +
            (cardWidth * itemsCount) +
            (marginBetweenElementsHorizontal * (itemsCount - 1))

    val maxScroll = (totalContentWidth - lineWidth).coerceAtLeast(0.dp)

// If all content fits on one screen, the first dot is enough
    if (maxScroll == 0.dp) return listOf(initialDot)

    val result = mutableListOf(initialDot)
    var currentCardPosition = 0
    var currentScroll = 0.dp

    val lastIndex = itemsCount - 1

    while (currentCardPosition < lastIndex && currentScroll < maxScroll) {
        val windowEnd = currentScroll + lineWidth

        // Find all elements intersecting the current visible window [windowStart, windowEnd]
        val visibleCardIndices = mutableListOf<Int>()

        for (i in (currentCardPosition + 1)..lastIndex) {
            val itemStart = paddingStart + (i * (cardWidth + marginBetweenElementsHorizontal))

            // An element is considered visible if its start is before the end of the current screen.
            if (itemStart < windowEnd) {
                visibleCardIndices.add(i)
            } else {
                break
            }
        }

        // If there are no new elements in the window or only one next one is visible, we take it;
        // if several are visible, we take the most recent of the visible ones.
        val nextTargetPos = when {
            visibleCardIndices.isEmpty() -> currentCardPosition + 1
            visibleCardIndices.size == 1 -> visibleCardIndices.first()
            else -> visibleCardIndices.last()
        }.coerceAtMost(lastIndex)

        // Calculate the offset for the found element
        val targetOffset = when (nextTargetPos) {
            0 -> -paddingStart
            lastIndex -> Int.MAX_VALUE.dp
            else -> -marginBetweenElementsHorizontal
        }

        result.add(ListDot(targetPos = nextTargetPos, offset = targetOffset))

        // Update the current position and scroll for the next step.
        currentCardPosition = nextTargetPos

        currentScroll = if (nextTargetPos == lastIndex) {
            maxScroll
        } else {
            val nextItemStart =
                paddingStart + (nextTargetPos * (cardWidth + marginBetweenElementsHorizontal))
            (nextItemStart + targetOffset).coerceAtLeast(0.dp)
        }
    }

    result[result.lastIndex] =
        result[result.lastIndex].copy(targetPos = itemsCount - 1, offset = paddingEnd)
    return result
}

@Stable
private class CarouselState(
    val dotList: List<ListDot>,
    val listState: LazyListState,
    private val density: Density,
    initialActiveDot: Int = 0
) {
    var activeDot by mutableIntStateOf(initialActiveDot)
        private set

    suspend fun scrollToDot(index: Int, animate: Boolean = true) {
        if (dotList.isEmpty()) return

        val targetIndex = index.coerceIn(0, dotList.lastIndex)
        val dot = dotList[targetIndex]

        activeDot = targetIndex

        if (animate) {
            listState.animateScrollToItem(
                index = dot.targetPos,
                scrollOffset = with(density) { dot.offset.toPx().toInt() },
            )
        } else {
            listState.scrollToItem(
                index = dot.targetPos,
                scrollOffset = with(density) { dot.offset.toPx().toInt() },
            )
        }

    }

    fun calculateTargetIndexForFling(velocity: Float): Int {
        if (dotList.isEmpty()) return 0

        val maxIndex = dotList.lastIndex

        return when {
            velocity > 300f -> (activeDot + 1).coerceAtMost(maxIndex)

            velocity < -300f -> (activeDot - 1).coerceAtLeast(0)

            else -> {
                val visibleItems = listState.layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return activeDot

                val startPadding = listState.layoutInfo.beforeContentPadding

                dotList.mapIndexed { dotIndex, dot ->
                    val item = visibleItems.find { it.index == dot.targetPos }
                    if (item != null) {
                        val dotOffsetPx = with(density) { dot.offset.toPx() }
                        val targetPosPx = startPadding + dotOffsetPx
                        dotIndex to abs(item.offset - targetPosPx)
                    } else {
                        dotIndex to Float.MAX_VALUE
                    }
                }.minByOrNull { it.second }?.first ?: activeDot
            }
        }
    }
}

private class CarouselFlingBehavior(
    private val carouselState: CarouselState,
    private val coroutineScope: CoroutineScope
) : FlingBehavior {

    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        val targetDotIndex = carouselState.calculateTargetIndexForFling(initialVelocity)

        coroutineScope.launch {
            carouselState.scrollToDot(targetDotIndex)
        }

        return 0f
    }
}

@Composable
private fun rememberCarouselState(
    dotList: List<ListDot>,
    listState: LazyListState = rememberLazyListState()
): CarouselState {
    val density = LocalDensity.current
    return remember(dotList, listState, density) {
        CarouselState(
            dotList = dotList,
            listState = listState,
            density = density
        )
    }
}

@Composable
private fun rememberCarouselSnapFlingBehavior(
    carouselState: CarouselState,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): FlingBehavior {
    return remember(carouselState) {
        CarouselFlingBehavior(carouselState, coroutineScope)
    }
}