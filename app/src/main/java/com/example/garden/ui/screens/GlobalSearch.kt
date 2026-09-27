package com.example.garden.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.garden.GlobalSearchState
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.OverLayLayer
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.database.entities.CardSize
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.LayoutType
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.database.entities.PageType
import com.example.garden.database.entities.RecentQueriesEntity
import com.example.garden.database.entities.SizeType
import com.example.garden.ui.components.FlatGridCarousel
import com.example.garden.ui.components.ShapesLoadingIndicator
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.SearchIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowInfo
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.utils.search.searchInList
import com.example.garden.viewmodel.ListSearchState
import com.example.garden.viewmodel.RecentQueriesViewModel
import com.example.garden.viewmodel.ResultSenderViewModel
import com.example.garden.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun GlobalSearch(
    layer: Layer.OverLay,
    resultSenderViewModel: ResultSenderViewModel,
    closeLayer: () -> Unit,
    recentQueriesViewModel: RecentQueriesViewModel,
    searchViewModel: SearchViewModel,
    onCardClick: (ObjectData.Card) -> Unit
) {
    if (layer.info is OverLayLayer.GlobalSearch) {
        val haptic = LocalHapticFeedback.current
        val focusManager = LocalFocusManager.current
        val density = LocalDensity.current

        val topInsetPx = WindowInsets.safeDrawing.getTop(density)
        val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
        val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)

        val topInset = with(density) { topInsetPx.toDp() }
        val rightInset = with(density) { rightInsetPx.toDp() }
        val leftInset = with(density) { leftInsetPx.toDp() }

        val searchState = rememberTextFieldState(initialText = "")

        val recentQueries = recentQueriesViewModel.getRecentQueries()
            .collectAsStateWithLifecycle(initialValue = emptyList())

        var isProgrammaticFocus by remember { mutableStateOf(false) }

        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit) {
            isProgrammaticFocus = true
            kotlinx.coroutines.delay(300.milliseconds)
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        val localBDSearchState =
            searchViewModel.localSearchFlow.collectAsStateWithLifecycle(initialValue = ListSearchState.Loading)

        val localBDSearch = remember(localBDSearchState.value) {
            if (localBDSearchState.value is ListSearchState.Success) {
                (localBDSearchState.value as ListSearchState.Success).items
            } else {
                emptyList()
            }
        }

        val localBDSearchCarouselName = stringResource(R.string.OnDevice)

        val aniLibertySearchState =
            searchViewModel.aniLibriaSearchFlow.collectAsStateWithLifecycle(initialValue = ListSearchState.Loading)

        val aniLibertySearch = remember(aniLibertySearchState.value) {
            if (aniLibertySearchState.value is ListSearchState.Success) {
                (aniLibertySearchState.value as ListSearchState.Success).items
            } else {
                emptyList()
            }
        }

        val aniLibertySearchCarouselName = stringResource(R.string.AniLibria)

        val musicSearch = remember {
            mutableStateOf<List<ObjectEntity>>(emptyList())
        }

        val musicSearchCarouselName = stringResource(R.string.Music)

        val order = remember(
            localBDSearch,
            aniLibertySearch,
            musicSearch.value
        ) {
            listOf(
                Pair(localBDSearch, localBDSearchCarouselName),
                Pair(aniLibertySearch, aniLibertySearchCarouselName),
                Pair(musicSearch.value, musicSearchCarouselName)
            ).sortedByDescending { it.first.size }
        }

        var offers by remember { mutableStateOf(emptyList<RecentQueriesEntity>()) }

        LaunchedEffect(localBDSearch, aniLibertySearch, musicSearch.value) {
            val query = searchState.text.toString()
            val rawList = localBDSearch + aniLibertySearch + musicSearch.value

            val res = withContext(Dispatchers.IO) {
                searchInList(
                    rawList.map { it.name },
                    query,
                    0
                ).mapIndexed { index, string ->
                    RecentQueriesEntity(
                        query = string,
                        position = index
                    )
                }
            }

            offers = res
        }

        LaunchedEffect(searchState.text) {
            if (searchState.text.isEmpty()) {
                searchViewModel.clearGlobalSearch()
            } else {
                searchViewModel.globalSearch(
                    searchState.text.toString()
                )
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                searchViewModel.clearGlobalSearch()
            }
        }

        var uiState by remember { mutableStateOf(layer.info.state) }

        LaunchedEffect(Unit) {
            resultSenderViewModel.results.collect { (key, _) ->
                when (key) {
                    ResultKeys.GLOBAL_SEARCH_STATE_CHANGED_TO_SEARCHING -> {
                        uiState = GlobalSearchState.Searching
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .blockGestures()
                .clearFocus(focusManager)
                .background(
                    if (uiState == GlobalSearchState.Searching) MaterialTheme.colorScheme.background.copy(
                        alpha = 0f
                    ) else MaterialTheme.colorScheme.background
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (uiState == GlobalSearchState.Searching) MaterialTheme.spacing.large else 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.screenHorizontal + leftInset,
                        end = MaterialTheme.spacing.screenHorizontal + rightInset,
                        top = MaterialTheme.spacing.screenHorizontal + topInset
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

                Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

                OutlinedTextField(
                    state = searchState,
                    modifier = Modifier
                        .weight(1f, fill = true)
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                if (isProgrammaticFocus) {
                                    isProgrammaticFocus = false
                                } else {
                                    layer.info.state = GlobalSearchState.Searching
                                    uiState = GlobalSearchState.Searching
                                }
                            }
                        },
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,

                        unfocusedBorderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f),
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f),
                    ),
                    contentPadding = PaddingValues(MaterialTheme.spacing.medium),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.Search),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = SearchIco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                        )
                    },
                    trailingIcon = {
                        if (searchState.text.isNotEmpty()) {
                            Icon(
                                imageVector = CloseIco,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.iconMedium)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                                searchState.clearText()
                                                focusRequester.requestFocus()
                                                keyboardController?.show()
                                            }
                                        )
                                    }
                            )
                        }
                    },
                    onKeyboardAction = { _ ->
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (searchState.text.isNotEmpty()) {
                            layer.info.state = GlobalSearchState.Found
                            uiState = GlobalSearchState.Found
                            recentQueriesViewModel.addOrUpRecentQuery(
                                RecentQueriesEntity(
                                    query = searchState.text.toString(),
                                    position = 0
                                )
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    lineLimits = TextFieldLineLimits.SingleLine
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
                    .padding(
                        start = if (uiState == GlobalSearchState.Searching) MaterialTheme.spacing.screenHorizontal + leftInset else 0.dp,
                        end = if (uiState == GlobalSearchState.Searching) MaterialTheme.spacing.screenHorizontal + rightInset else 0.dp
                    )
                    .clip(MaterialTheme.shapes.large)
                    .background(if (uiState == GlobalSearchState.Searching) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(
                    start = if (uiState == GlobalSearchState.Searching) MaterialTheme.spacing.screenHorizontal else 0.dp,
                    end = if (uiState == GlobalSearchState.Searching) MaterialTheme.spacing.screenHorizontal else 0.dp,
                    top = MaterialTheme.spacing.screenHorizontal,
                    bottom = MaterialTheme.spacing.screenHorizontal
                )
            ) {
                if (uiState == GlobalSearchState.Searching) {
                    if (searchState.text.isEmpty() || offers.isEmpty()) {
                        if (recentQueries.value.isNotEmpty()) {
                            item("label") {
                                Text(
                                    text = stringResource(R.string.RecentQueries),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Spacer(
                                    modifier = Modifier
                                        .height(MaterialTheme.spacing.medium)
                                        .fillMaxWidth()
                                )
                            }

                            items(
                                count = recentQueries.value.size,
                                contentType = { "recentQuery" }
                            ) { index ->
                                val recentQuery = recentQueries.value[index]
                                Query(
                                    isRecentQuery = true,
                                    onDeleteQuery = {
                                        recentQueriesViewModel.deleteRecentQuery(
                                            recentQuery
                                        )
                                    },
                                    onClick = {
                                        focusManager.clearFocus()
                                        searchState.setTextAndPlaceCursorAtEnd(recentQuery.query)
                                        recentQueriesViewModel.addOrUpRecentQuery(recentQuery)
                                        layer.info.state = GlobalSearchState.Found
                                        uiState = GlobalSearchState.Found
                                    },
                                    query = recentQuery
                                )
                            }
                        }
                    } else {
                        items(
                            count = offers.size,
                            contentType = { "offer" }
                        ) { index ->
                            val offer = offers[index]

                            Query(
                                isRecentQuery = false,
                                onDeleteQuery = {},
                                onClick = {
                                    focusManager.clearFocus()
                                    searchState.setTextAndPlaceCursorAtEnd(offer.query)
                                    recentQueriesViewModel.addOrUpRecentQuery(offer)
                                    layer.info.state = GlobalSearchState.Found
                                    uiState = GlobalSearchState.Found
                                },
                                query = offer
                            )
                        }
                    }
                } else {
                    if (localBDSearchState.value is ListSearchState.Empty && aniLibertySearchState.value is ListSearchState.Empty) {
                        item("noResults") {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                textAlign = TextAlign.Center,
                                text = stringResource(R.string.NoResults),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                            )
                        }
                    } else {
                        items(
                            count = 3
                        ) { index ->
                            val item = order[index]
                            SearchSelection(
                                name = item.second,
                                cards = item.first,
                                onClick = onCardClick,
                                isLoading = (item.second == aniLibertySearchCarouselName && aniLibertySearchState.value is ListSearchState.Loading) ||
                                        (item.second == localBDSearchCarouselName && localBDSearchState.value is ListSearchState.Loading)
                            )

                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(MaterialTheme.spacing.extraLarge)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Query(
    isRecentQuery: Boolean,
    onDeleteQuery: () -> Unit,
    onClick: () -> Unit,
    query: RecentQueriesEntity
) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        focusManager.clearFocus()
                        onClick()
                    }
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = query.query,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))

        if (isRecentQuery) {
            Icon(
                imageVector = CloseIco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(MaterialTheme.dimens.iconMedium)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onDeleteQuery()
                            }
                        )
                    }
            )
        }
    }
}

@Composable
private fun SearchSelection(
    name: String,
    cards: List<ObjectEntity>,
    onClick: (ObjectData.Card) -> Unit,
    isLoading: Boolean,
) {
    val density = LocalDensity.current

    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)

    val rightInset = with(density) { rightInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }

    val cardWidth =
        (MaterialTheme.windowInfo.widthDp - MaterialTheme.spacing.screenHorizontal * 2 - rightInset - leftInset - MaterialTheme.spacing.extraLarge)

    val cards = remember(cards) {
        cards.map { it.copy(isUserCreated = false) }.map { it.info.injectLocalObjectEntityData(it) }
            .filterIsInstance<ObjectData.Card>()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.spacing.screenHorizontal + leftInset,
                    end = MaterialTheme.spacing.screenHorizontal + rightInset
                ),
            text = name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        val parent = ObjectData.Carousel(
            id = "${EntitySourceType.Local.name}_-1",
            name = "",
            isUserCreated = false,
            childsShowName = true,
            childsShowAuthor = true,
            childsShowAlreadyWatchedLine = false,
            childsCornerRadius = SizeType.SMALL,
            layoutType = LayoutType.CAROUSEL_FROM_FLAT_GRID,
            childsNamePosition = 1,
            childsSize = CardSize.SMALL,
            dovodchik = true
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.screenHorizontal + leftInset,
                        end = MaterialTheme.spacing.screenHorizontal + rightInset
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    ShapesLoadingIndicator()
                }
            }
        } else {
            if (cards.isNotEmpty()) {
                FlatGridCarousel(
                    isSearchSelectionMode = true,
                    cardWidth = cardWidth,
                    paddingStart = leftInset + MaterialTheme.spacing.screenHorizontal,
                    paddingEnd = rightInset + MaterialTheme.spacing.screenHorizontal,
                    marginBetweenElements = MaterialTheme.spacing.medium,
                    allCardAmount = cards.size,
                    layer = Layer.MainPage(
                        page = PageType.Home,
                        scrollPositionCarousels = mutableMapOf(),
                        mainRecyclerScrollPosition = 0
                    ),
                    childs = cards,
                    parent = parent,
                    maxLines = 6,
                    cardAspectRatio = 0f,
                    onEditCard = {},
                    onClickCard = onClick,
                    onDeleteCard = {}
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(
                            start = MaterialTheme.spacing.screenHorizontal + leftInset,
                            end = MaterialTheme.spacing.screenHorizontal + rightInset
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.NoResults),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                        )
                    }
                }
            }
        }
    }
}
