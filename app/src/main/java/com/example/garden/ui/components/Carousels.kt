package com.example.garden.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.garden.Layer
import com.example.garden.database.LayoutType
import com.example.garden.database.ObjectData
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.toDp
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun StandardCarousel(
    layer: Layer.MainPage,
    childs: List<ObjectData.Card>,
    parent: ObjectData.Carousel,
    cardAspectRatio: Float,
    cardWidth: Dp,
    paddingStart: Dp,
    paddingEnd: Dp,
    marginBetweenElements: Dp,
    onEditCard: (ObjectData.Card) -> Unit,
    onClickCard: (ObjectData.Card) -> Unit,
    onDeleteCard: (Long) -> Unit
) {
    val savedCardIndex = layer.scrollPositionCarousels[parent.id] ?: 0

    val listState = key(parent.id) {
        rememberLazyListState(
            initialFirstVisibleItemIndex = savedCardIndex,
            initialFirstVisibleItemScrollOffset = 0
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (listState.isScrollInProgress) {
                    layer.scrollPositionCarousels[parent.id] = index
                }
            }
    }

    LaunchedEffect(savedCardIndex, childs.size) {
        if (!listState.isScrollInProgress && childs.size >= savedCardIndex + 1) {
            if (listState.firstVisibleItemIndex != savedCardIndex) {
                listState.scrollToItem(savedCardIndex, 0)
            }
        }
    }

    val cardsTextBaseHeight = MaterialTheme.typography.titleMedium.lineHeight.toDp()

    val cardsTextHeight = remember(parent.childsShowName, parent.childsShowAuthor) {
        if (parent.childsShowName && !parent.childsShowAuthor) {
            cardsTextBaseHeight * 2
        } else if (parent.childsShowAuthor && !parent.childsShowName) {
            cardsTextBaseHeight
        } else if (parent.childsShowName) {
            cardsTextBaseHeight * 3
        } else {
            0.dp
        }
    }

    val cardHeight = remember(
        cardWidth,
        cardAspectRatio,
        parent.childsShowName,
        parent.childsShowAuthor,
        cardsTextHeight
    ) {
        (cardWidth / cardAspectRatio) + cardsTextHeight
    }

    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LazyRow(
        state = listState,
        flingBehavior = if (parent.dovodchik) snapFlingBehavior else ScrollableDefaults.flingBehavior(),
        modifier = Modifier
            .height(cardHeight)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(marginBetweenElements),
        contentPadding = PaddingValues(start = paddingStart, end = paddingEnd)
    ) {
        items(
            count = childs.size,
            key = { if (childs[it].id == 0L) "temp_${childs[it].hashCode()}" else childs[it].id }
        ) { index ->
            childs[index].let { card ->
                Card(
                    modifier = Modifier.width(cardWidth),
                    aspectRatio = cardAspectRatio,
                    showName = parent.childsShowName,
                    showAuthor = parent.childsShowAuthor,
                    showAlreadyWatchedLine = parent.childsShowAlreadyWatchedLine,
                    author = card.author,
                    name = card.name,
                    image = card.image,
                    cornerRadius = parent.childsCornerRadius,
                    layoutType = LayoutType.DEFAULT,
                    namePosition = parent.childsNamePosition,
                    alreadyWatched = 0L,
                    length = 0L,
                    onEdit = { onEditCard(card) },
                    onDelete = { onDeleteCard(card.id) },
                    onClick = { onClickCard(card) }
                )
            }
        }
    }
}

@Composable
fun FlatGridCarousel(
    layer: Layer.MainPage,
    allCardAmount: Int,
    childs: List<ObjectData.Card>,
    maxLines: Int,
    parent: ObjectData.Carousel,
    cardAspectRatio: Float,
    cardWidth: Dp,
    paddingStart: Dp,
    paddingEnd: Dp,
    marginBetweenElements: Dp,
    onEditCard: (ObjectData.Card) -> Unit,
    onClickCard: (ObjectData.Card) -> Unit,
    onDeleteCard: (Long) -> Unit
) {
    val savedCardIndex = layer.scrollPositionCarousels[parent.id] ?: 0

    val gridState = key(parent.id) {
        rememberLazyGridState(
            initialFirstVisibleItemIndex = savedCardIndex,
            initialFirstVisibleItemScrollOffset = 0
        )
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex }
            .collect { index ->
                if (gridState.isScrollInProgress) {
                    layer.scrollPositionCarousels[parent.id] = index
                }
            }
    }

    LaunchedEffect(savedCardIndex, childs.size) {
        if (!gridState.isScrollInProgress && childs.size >= savedCardIndex + 1) {
            if (gridState.firstVisibleItemIndex != savedCardIndex) {
                gridState.scrollToItem(savedCardIndex, 0)
            }
        }
    }

    val cardHeight = MaterialTheme.dimens.flatGridItemHeight
    val totalHeight =
        (cardHeight * maxLines.coerceAtMost(allCardAmount))

    val snapFlingBehavior = rememberSinglePageGridSnapFlingBehavior(gridState = gridState)

    LazyHorizontalGrid(
        rows = GridCells.Fixed(maxLines.coerceAtMost(allCardAmount)),
        state = gridState,
        flingBehavior = if (parent.dovodchik) snapFlingBehavior else ScrollableDefaults.flingBehavior(),
        modifier = Modifier.height(totalHeight),
        horizontalArrangement = Arrangement.spacedBy(marginBetweenElements),
        contentPadding = PaddingValues(
            start = paddingStart - (MaterialTheme.spacing.screenHorizontal / 2),
            end = paddingEnd - (MaterialTheme.spacing.screenHorizontal / 2)
        )
    ) {
        items(
            count = childs.size,
            key = { if (childs[it].id == 0L) "temp_${childs[it].hashCode()}" else childs[it].id }
        ) { index ->
            childs[index].let { card ->
                Card(
                    modifier = Modifier.width(cardWidth),
                    aspectRatio = cardAspectRatio,
                    showName = parent.childsShowName,
                    showAuthor = parent.childsShowAuthor,
                    showAlreadyWatchedLine = parent.childsShowAlreadyWatchedLine,
                    author = card.author,
                    name = card.name,
                    image = card.image,
                    cornerRadius = parent.childsCornerRadius,
                    layoutType = LayoutType.FLAT_GRID_ITEM,
                    namePosition = parent.childsNamePosition,
                    alreadyWatched = 0L,
                    length = 0L,
                    onEdit = { onEditCard(card) },
                    onDelete = { onDeleteCard(card.id) },
                    onClick = { onClickCard(card) }
                )
            }
        }
    }
}

@Composable
fun rememberSinglePageGridSnapFlingBehavior(gridState: LazyGridState): FlingBehavior {
    val snapLayoutInfoProvider = remember(gridState) {
        val baseProvider = SnapLayoutInfoProvider(lazyGridState = gridState)
        object : SnapLayoutInfoProvider by baseProvider {
            override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float {
                return 0f
            }
        }
    }

    return rememberSnapFlingBehavior(snapLayoutInfoProvider)
}

@Composable
fun PagedGridCarousel(
    layer: Layer.MainPage,
    gridWidth: Dp,
    allCardAmount: Int,
    childs: List<ObjectData.Card>,
    parent: ObjectData.Carousel,
    objectsInLine: Int,
    maxLines: Int,
    cardAspectRatio: Float,
    marginBetweenElements: Dp,
    onEditCard: (ObjectData.Card) -> Unit,
    onClickCard: (ObjectData.Card) -> Unit,
    onDeleteCard: (Long) -> Unit
) {
    val itemsPerPage = (objectsInLine * maxLines).coerceAtLeast(1)
    val savedCardIndex = layer.scrollPositionCarousels[parent.id] ?: 0
    val targetPage = savedCardIndex / itemsPerPage
    val pageCount =
        if (childs.isEmpty()) 1 else (childs.size + itemsPerPage - 1) / itemsPerPage

    val pagerState = key(parent.id) {
        rememberPagerState(
            initialPage = targetPage,
            pageCount = { pageCount }
        )
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                if (pagerState.isScrollInProgress) {
                    layer.scrollPositionCarousels[parent.id] = page * itemsPerPage
                }
            }
    }

    LaunchedEffect(targetPage, childs.size) {
        if (!pagerState.isScrollInProgress && targetPage < pageCount) {
            if (pagerState.currentPage != targetPage) {
                pagerState.scrollToPage(targetPage)
            }
        }
    }

    val cardWidth = remember(
        gridWidth,
        marginBetweenElements,
        objectsInLine
    ) { (gridWidth - marginBetweenElements * (objectsInLine - 1)) / objectsInLine }

    val cardsTextBaseHeight = MaterialTheme.typography.titleMedium.lineHeight.toDp()

    val cardsTextHeight = remember(parent.childsShowName, parent.childsShowAuthor) {
        if (parent.childsShowName && !parent.childsShowAuthor) {
            cardsTextBaseHeight * 2
        } else if (parent.childsShowAuthor && !parent.childsShowName) {
            cardsTextBaseHeight
        } else if (parent.childsShowName) {
            cardsTextBaseHeight * 3
        } else {
            0.dp
        }
    }

    val cardHeight = remember(
        cardWidth,
        cardAspectRatio,
        parent.childsShowName,
        parent.childsShowAuthor,
        cardsTextHeight
    ) {
        (cardWidth / cardAspectRatio) + cardsTextHeight
    }

    val maxValueOfMaxLines = ceil(allCardAmount.toFloat() / objectsInLine.toFloat()).toInt()

    val totalHeight =
        (cardHeight * maxLines.coerceAtMost(maxValueOfMaxLines)) + (8.dp * (maxLines.coerceAtMost(
            maxValueOfMaxLines
        ) - 1))

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight),
            pageSpacing = 0.dp,
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            val startIndex = pageIndex * itemsPerPage
            val pageItems = (0 until itemsPerPage).mapNotNull { offset ->
                val index = startIndex + offset
                if (index < childs.size) childs[index] else null
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(marginBetweenElements)
            ) {
                pageItems.chunked(objectsInLine).forEach { rowItems ->
                    Row(
                        modifier = Modifier.width(gridWidth),
                        horizontalArrangement = Arrangement.spacedBy(marginBetweenElements)
                    ) {
                        rowItems.forEach { card ->
                            Card(
                                modifier = Modifier.width(cardWidth),
                                aspectRatio = cardAspectRatio,
                                showName = parent.childsShowName,
                                showAuthor = parent.childsShowAuthor,
                                showAlreadyWatchedLine = parent.childsShowAlreadyWatchedLine,
                                author = card.author,
                                name = card.name,
                                image = card.image,
                                cornerRadius = parent.childsCornerRadius,
                                layoutType = LayoutType.DEFAULT,
                                namePosition = parent.childsNamePosition,
                                alreadyWatched = 0L,
                                length = 0L,
                                onEdit = { onEditCard(card) },
                                onDelete = { onDeleteCard(card.id) },
                                onClick = { onClickCard(card) }
                            )
                        }

                        repeat(objectsInLine - rowItems.size) {
                            Spacer(
                                modifier = Modifier
                                    .width(cardWidth)
                                    .aspectRatio(cardAspectRatio)
                            )
                        }
                    }
                }
            }
        }

        if (pageCount > 1 && parent.showDovodchikDots) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            PagerDotsIndicator(
                pageCount = pageCount,
                currentPage = pagerState.currentPage,
                onDotClick = { targetPage ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(targetPage)
                    }
                }
            )
        }
    }
}

@Composable
fun PagerDotsIndicator(
    pageCount: Int,
    currentPage: Int,
    onDotClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.height(MaterialTheme.dimens.dotSize),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .width(if (isSelected) MaterialTheme.dimens.activeDotWidth else MaterialTheme.dimens.dotSize)
                    .height(MaterialTheme.dimens.dotSize)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    .clickable { onDotClick(index) }
            )
        }
    }
}

@Composable
fun GridCarousel(
    childs: List<ObjectData.Card>,
    parent: ObjectData.Carousel,
    objectsInLine: Int,
    maxLines: Int? = null,
    cardAspectRatio: Float,
    paddingStart: Dp,
    paddingEnd: Dp,
    marginBetweenElements: Dp,
    onEditCard: (ObjectData.Card) -> Unit,
    onClickCard: (ObjectData.Card) -> Unit,
    onDeleteCard: (Long) -> Unit
) {
    val totalRows = (childs.size + objectsInLine - 1) / objectsInLine

    val visibleRows = if (maxLines != null && maxLines > 0) {
        minOf(totalRows, maxLines)
    } else {
        totalRows
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingStart, end = paddingEnd),
        verticalArrangement = Arrangement.spacedBy(marginBetweenElements)
    ) {
        for (rowIndex in 0 until visibleRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(marginBetweenElements)
            ) {
                for (colIndex in 0 until objectsInLine) {
                    val itemIndex = rowIndex * objectsInLine + colIndex

                    if (itemIndex < childs.size) {
                        val card = childs[itemIndex]

                        Card(
                            modifier = Modifier.weight(1f),
                            aspectRatio = cardAspectRatio,
                            showName = parent.childsShowName,
                            showAuthor = parent.childsShowAuthor,
                            showAlreadyWatchedLine = parent.childsShowAlreadyWatchedLine,
                            author = card.author,
                            name = card.name,
                            image = card.image,
                            cornerRadius = parent.childsCornerRadius,
                            layoutType = parent.layoutType,
                            namePosition = parent.childsNamePosition,
                            alreadyWatched = 0L,
                            length = 0L,
                            onEdit = { onEditCard(card) },
                            onDelete = { onDeleteCard(card.id) },
                            onClick = { onClickCard(card) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CarouselFactory(
    layer: Layer.MainPage,
    carousel: ObjectData.Carousel,
    childs: List<ObjectData.Card>,
    cardWidth: Dp,
    cardAspectRatio: Float,
    paddingStart: Dp,
    paddingEnd: Dp,
    marginBetweenElements: Dp,
    onEditCard: (ObjectData.Card) -> Unit,
    onClickCard: (ObjectData.Card) -> Unit,
    onDeleteCard: (Long) -> Unit
) {
    when (carousel.layoutType) {
        LayoutType.CAROUSEL_GRID -> {
            GridCarousel(
                childs = childs,
                parent = carousel,
                objectsInLine = carousel.objectsInOneLine ?: 10,
                maxLines = carousel.maxLines,
                cardAspectRatio = cardAspectRatio,
                paddingStart = paddingStart,
                paddingEnd = paddingEnd,
                marginBetweenElements = marginBetweenElements,
                onEditCard = onEditCard,
                onClickCard = onClickCard,
                onDeleteCard = onDeleteCard
            )
        }

        LayoutType.CAROUSEL_FROM_FLAT_GRID -> {
            FlatGridCarousel(
                layer = layer,
                allCardAmount = childs.size,
                childs = childs,
                maxLines = carousel.maxLines ?: 3,
                parent = carousel,
                cardAspectRatio = cardAspectRatio,
                cardWidth = cardWidth,
                paddingStart = paddingStart,
                paddingEnd = paddingEnd,
                marginBetweenElements = marginBetweenElements,
                onEditCard = onEditCard,
                onClickCard = onClickCard,
                onDeleteCard = onDeleteCard
            )
        }

        LayoutType.CAROUSEL_FROM_GRID -> {
            PagedGridCarousel(
                layer = layer,
                allCardAmount = childs.size,
                childs = childs,
                parent = carousel,
                objectsInLine = carousel.objectsInOneLine ?: 10,
                maxLines = carousel.maxLines ?: 10,
                cardAspectRatio = cardAspectRatio,
                gridWidth = cardWidth,
                marginBetweenElements = marginBetweenElements,
                onEditCard = onEditCard,
                onClickCard = onClickCard,
                onDeleteCard = onDeleteCard
            )
        }

        else -> {
            StandardCarousel(
                layer = layer,
                childs = childs,
                parent = carousel,
                cardAspectRatio = cardAspectRatio,
                cardWidth = cardWidth,
                paddingStart = paddingStart,
                paddingEnd = paddingEnd,
                marginBetweenElements = marginBetweenElements,
                onEditCard = onEditCard,
                onClickCard = onClickCard,
                onDeleteCard = onDeleteCard
            )
        }
    }
}
