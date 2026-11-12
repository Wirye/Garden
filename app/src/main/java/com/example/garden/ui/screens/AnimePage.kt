package com.example.garden.ui.screens

import android.os.Parcelable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.garden.AniLibriaClient
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.database.entities.CardSize
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.EpisodeInfo
import com.example.garden.database.entities.GenreAge
import com.example.garden.database.entities.GenreEpisodes
import com.example.garden.database.entities.GenreSezon
import com.example.garden.database.entities.GenreYear
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.YearSezon
import com.example.garden.ui.components.AppAsyncImage
import com.example.garden.ui.components.CardPreview
import com.example.garden.ui.components.CardWatchedProgressLine
import com.example.garden.ui.components.CloseButton
import com.example.garden.ui.components.DropDownMenuWithBlur
import com.example.garden.ui.components.HelpDialog
import com.example.garden.ui.components.PopupMenuItem
import com.example.garden.ui.components.ShapesLoadingIndicator
import com.example.garden.ui.components.ShapesPullToRefreshIndicator
import com.example.garden.ui.components.StrokeText
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.MoreVertIco
import com.example.garden.ui.components.icons.PlayArrowFilledIco
import com.example.garden.ui.components.icons.SearchIco
import com.example.garden.ui.components.icons.StarIco
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.theme.windowInfo
import com.example.garden.ui.theme.windowSizeClass
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.ui.utils.formatAuthors
import com.example.garden.ui.utils.getCardWidth
import com.example.garden.ui.utils.getLargeCardWidth
import com.example.garden.ui.utils.toDp
import com.example.garden.ui.utils.toEpisodeInfo
import com.example.garden.utils.isLocalId
import com.example.garden.utils.isWebId
import com.example.garden.utils.search.searchInList
import com.example.garden.utils.toLongId
import com.example.garden.utils.toObjectEntity
import com.example.garden.viewmodel.MainViewModel
import com.example.garden.viewmodel.ObjectLinksViewModel
import com.example.garden.viewmodel.ResultSenderViewModel
import com.github.wirye.anilibriakt.model.Episode
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.Collections.min
import kotlin.time.Duration.Companion.seconds
import android.graphics.Color as AndroidColor

private const val HeaderItemsCount = 3
private const val MaxColumns = 6
private const val EpisodeAspectRatio = 1.78f
private val EpisodeMinWidth = 250.dp
private val EpisodeUnwatchedBlurRadius = 25.dp

@Parcelize
enum class AnimePageUiState : Parcelable {
    Episodes, Linked
}

private enum class EpisodesViewMode { Grid, List }

private sealed class ListState<T> {
    data class Loading<T>(val nothing: Int = 0) : ListState<T>()
    data class Loaded<T>(val episodes: List<T>) : ListState<T>()
}

@Composable
fun AnimePage(
    layer: Layer.AnimePage,
    mainViewModel: MainViewModel,
    resultSenderViewModel: ResultSenderViewModel,
    objectLinksViewModel: ObjectLinksViewModel,
    closePage: () -> Unit,
    onOpenEpisode: (List<EpisodeInfo>, Int?, String) -> Unit = { _, _, _ -> },
    onOpenCard: (ObjectData.Card.Anime) -> Unit = {},
) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val tick = { haptic.performHapticFeedback(HapticFeedbackType.ContextClick) }

    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
    val bottomInsetPx = WindowInsets.safeDrawing.getBottom(density)

    val topInset = with(density) { topInsetPx.toDp() }
    val rightInset = with(density) { rightInsetPx.toDp() }
    val leftInset = with(density) { leftInsetPx.toDp() }
    val bottomInset = with(density) { bottomInsetPx.toDp() }
    var updater by remember { mutableStateOf(false) }

    var isInfoRefreshing by remember { mutableStateOf(false) }
    var isContentRefreshing by remember { mutableStateOf(false) }
    val isRefreshing = isInfoRefreshing || isContentRefreshing

    var playerClosedKey by remember { mutableStateOf("-9-023108328834717410 fully bred") }

    LaunchedEffect(Unit) {
        resultSenderViewModel.results.collect { (key, _) ->
            when (key) {
                playerClosedKey -> {
                    updater = !updater
                }
            }
        }
    }

    val card =
        if (layer.card.id.isLocalId() && (layer.card.id.toLongId() != 0L || layer.card.id.toLongId() != -1L))
            (mainViewModel.getObjectByIdAsFlow(layer.card.id.toLongId())
                .collectAsStateWithLifecycle(initialValue = null).value?.info?.takeIf { it is ObjectData.Card.Anime }) as? ObjectData.Card.Anime
        else layer.card

    val genre = remember(card) { mutableStateOf(card?.genre ?: emptyList()) }

    if (layer.card.id.isWebId() && layer.card.anilibriaCardId != null) {
        LaunchedEffect(layer.card, updater) {
            val tracked = isInfoRefreshing
            val loadJob = coroutineScope.launch {
                withTimeout(10.seconds) {
                    AniLibriaClient.releases.getRelease(layer.card.anilibriaCardId).map {
                        it.toObjectEntity(
                            layer.card.position,
                            sourceType = EntitySourceType.Web
                        ).info as? ObjectData.Card.Anime
                    }
                }.onSuccess {
                    if (it != null) {
                        genre.value = it.genre
                    }
                }
            }
            if (tracked) loadJob.invokeOnCompletion { isInfoRefreshing = false }
        }
    }

    if (card != null) {
        var episodes by remember(card) { mutableStateOf<ListState<EpisodeInfo>>(ListState.Loading()) }
        val continueWith by remember(episodes) { mutableStateOf(episodes.continueWith()) }

        var isInFavourite by remember(Unit) { mutableStateOf(Pair(false, false)) }

        LaunchedEffect(card, updater) {
            if (card.anilibriaCardId == null) {
                isInFavourite = Pair(card.favourite, true)
            } else {
                coroutineScope.launch {
                    withTimeout(10.seconds) {
                        AniLibriaClient.account.favorites.getIdsOfFavoriteReleases()
                    }.onSuccess {
                        isInFavourite = Pair(it.contains(card.anilibriaCardId), true)
                    }
                }
            }
        }

        suspend fun updateFavourite(newValue: Boolean) {
            isInFavourite = isInFavourite.copy(first = newValue)
            if (card.anilibriaCardId == null && layer.card.id.isLocalId() && layer.card.id.toLongId() != 0L && layer.card.id.toLongId() != -1L) {
                mainViewModel.updateFavourite(layer.card.id.toLongId(), newValue)
            } else if (card.anilibriaCardId != null) {
                if (newValue) {
                    AniLibriaClient.account.favorites.addReleaseToFavorites(card.anilibriaCardId)
                } else {
                    AniLibriaClient.account.favorites.removeReleaseFromFavorites(card.anilibriaCardId)
                }
            }
        }

        val onFavorite: () -> Unit = {
            if (isInFavourite.second) {
                coroutineScope.launch {
                    updateFavourite(!isInFavourite.first)
                }
            }
        }

        var uiState by remember(layer) { mutableStateOf(layer.uiState) }
        var webGroupsObjects by remember(layer) {
            mutableStateOf<ListState<ObjectData.Card.Anime>>(
                ListState.Loading()
            )
        }
        val groups =
            (if (card.id.isLocalId()) objectLinksViewModel.getRelatedObjects(card.id.toLongId()) else flowOf(
                emptyList()
            )).collectAsStateWithLifecycle(emptyList()).value.map {
                it.info.injectLocalObjectEntityData(
                    it
                )
            }.filterIsInstance<ObjectData.Card.Anime>()

        val comparedGroups by remember(groups, webGroupsObjects) {
            mutableStateOf(groups + if (webGroupsObjects is ListState.Loaded) (webGroupsObjects as ListState.Loaded).episodes.filter {
                it.anilibriaCardId !in groups.map { card -> card.anilibriaCardId }.toSet()
            } else emptyList())
        }

        val finalGroups by remember(comparedGroups) { mutableStateOf(comparedGroups.filter { card.anilibriaCardId != null && it.anilibriaCardId != card.anilibriaCardId }) }

        LaunchedEffect(card, updater) {
            val tracked = isContentRefreshing
            val loadJob = coroutineScope.launch {
                if (card.anilibriaCardId != null) {
                    val episodes = async {
                        withTimeout(10.seconds) {
                            AniLibriaClient.releases.getRelease(card.anilibriaCardId)
                        }.onFailure {
                            // при неудачном обновлении не затираем уже загруженный список
                            if (episodes is ListState.Loading) {
                                episodes = ListState.Loaded(card.episodesList.asReversed())
                            }
                        }.onSuccess {
                            val webEpisodes = it.episodes
                            if (webEpisodes != null) {
                                var newWebEpisodes: List<Triple<Float?, Boolean, Episode>> =
                                    emptyList()
                                withTimeout(10.seconds) {
                                    AniLibriaClient.releases.getReleaseEpisodesUserWatchedTimecodes(
                                        card.anilibriaCardId
                                    )
                                        .onSuccess { timecodes ->
                                            newWebEpisodes = webEpisodes.map { episode ->
                                                Triple(
                                                    timecodes.firstOrNull { timeCode -> timeCode.episodeId == episode.episodeId }?.time,
                                                    (timecodes.firstOrNull { timeCode -> timeCode.episodeId == episode.episodeId }?.isWatched)
                                                        ?: false,
                                                    episode
                                                )
                                            }
                                        }
                                        .onFailure {
                                            newWebEpisodes = webEpisodes.map { episode ->
                                                Triple(
                                                    0f,
                                                    false,
                                                    episode
                                                )
                                            }
                                        }
                                }
                                val localEps = card.episodesList
                                episodes =
                                    ListState.Loaded((newWebEpisodes.mapIndexed { index, episode ->
                                        episode.third.toEpisodeInfo(
                                            index + 1,
                                            localEps,
                                            watched = episode.second,
                                            time = episode.first
                                        )
                                    } + localEps.filter { ep -> ep.position > webEpisodes.size }
                                        .sortedBy { ep -> ep.position }).asReversed())
                            } else {
                                episodes = ListState.Loaded(card.episodesList.asReversed())
                            }
                        }
                    }

                    val groups = async {
                        withTimeout(10.seconds) {
                            AniLibriaClient.releases.franchises.getFranchisesForRelease(card.anilibriaCardId)
                        }.onFailure {
                            if (webGroupsObjects is ListState.Loading) {
                                webGroupsObjects = ListState.Loaded(emptyList())
                            }
                        }.onSuccess { franchises ->
                            webGroupsObjects =
                                ListState.Loaded(franchises.asSequence().mapNotNull { franchise ->
                                    franchise.franchiseReleases?.map { frRelease -> frRelease.release }
                                }.flatten().mapIndexed { index, title ->
                                    title.toObjectEntity(
                                        position = index, sourceType = EntitySourceType.Web
                                    )
                                }.map {
                                    it.info.injectLocalObjectEntityData(it)
                                }.filterIsInstance<ObjectData.Card.Anime>().toList())
                        }
                    }

                    episodes.await()
                    groups.await()
                } else {
                    episodes = ListState.Loaded(card.episodesList.asReversed())
                    webGroupsObjects = ListState.Loaded(emptyList())
                }
            }
            if (tracked) loadJob.invokeOnCompletion { isContentRefreshing = false }
        }

        var viewMode by remember { mutableStateOf(EpisodesViewMode.Grid) }
        var isSearching by remember { mutableStateOf(false) }
        var query by remember { mutableStateOf("") }
        var isAboutOpened by remember { mutableStateOf(false) }
        var isViewMenuOpened by remember { mutableStateOf(false) }
        val gridState = rememberLazyGridState()
        val pullState = rememberPullToRefreshState()
        val searchFocus = remember { FocusRequester() }

        val width = MaterialTheme.windowInfo.widthDp
        val spacing = MaterialTheme.spacing.medium
        val horizontal = MaterialTheme.spacing.screenHorizontal
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

        val bannerHeight = bannerWidth * 1.5f
        val contentWidth = width - horizontal * 2 - leftInset - rightInset
        val isListLike = uiState == AnimePageUiState.Linked || viewMode == EpisodesViewMode.List
        val itemWidth = if (isListLike) CardSize.MEDIUM.toDp(
            maxCardWidth = contentWidth,
            cardType = ElementType.AnimeCard,
            windowWidthSizeClass = windowWidthClass
        ).width.coerceAtMost(contentWidth) else EpisodeMinWidth
        val columns =
            ((contentWidth + spacing) / (itemWidth + spacing)).toInt().coerceIn(1, MaxColumns)

        val visibleGroups = remember(finalGroups, query, uiState) {
            if (uiState != AnimePageUiState.Linked || query.isBlank()) finalGroups
            else {
                val names = searchInList(finalGroups.map { it.name }, query, 70).toSet()
                finalGroups.filter { it.name in names }
            }
        }

        val accents = rememberAccentColors(card.image)
        val firstBlob by animateColorAsState(
            accents.getOrNull(0) ?: MaterialTheme.colorScheme.primary
        )
        val secondBlob by animateColorAsState(
            accents.getOrNull(1) ?: MaterialTheme.colorScheme.tertiary
        )

        LaunchedEffect(isSearching) {
            if (isSearching) searchFocus.requestFocus()
        }

        LaunchedEffect(query) {
            if (uiState != AnimePageUiState.Episodes) return@LaunchedEffect
            val loaded = episodes as? ListState.Loaded ?: return@LaunchedEffect
            val number = query.toIntOrNull() ?: return@LaunchedEffect
            val index = loaded.episodes.indexOfFirst { it.position == number }
            if (index >= 0) gridState.animateScrollToItem(HeaderItemsCount + index)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .blockGestures()
                .clearFocus(focusManager)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawRect(
                            Brush.radialGradient(
                                listOf(firstBlob.copy(alpha = 0.35f), Color.Transparent),
                                center = Offset(size.width * 0.1f, size.height * 0.05f),
                                radius = size.maxDimension * 0.6f
                            )
                        )
                        drawRect(
                            Brush.radialGradient(
                                listOf(secondBlob.copy(alpha = 0.25f), Color.Transparent),
                                center = Offset(size.width * 0.95f, size.height * 0.4f),
                                radius = size.maxDimension * 0.5f
                            )
                        )
                    }
            )

            val banner: @Composable () -> Unit = {
                AppAsyncImage(
                    imageData = card.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(bannerWidth)
                        .aspectRatio(2f / 3f)
                        .clip(MaterialTheme.shapes.large)
                )
            }

            val info: @Composable (modifier: Modifier, genreModifier: Modifier) -> Unit =
                { modifier, genreModifier ->
                    Column(
                        modifier,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                    ) {
                        Column {
                            Text(
                                text = card.name.ifEmpty { stringResource(R.string.withoutName) },
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = listOf(card.author).formatAuthors()
                                    .ifEmpty { stringResource(R.string.studioIsUnknown) },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        val chips = genre.value

                        if (chips.isNotEmpty()) {
                            FlowRow(
                                modifier = genreModifier
                                    .heightIn(min = 32.dp),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                                maxLines = 2
                            ) {
                                chips.forEachIndexed { _, genre ->
                                    val color = Color(genre.colorHex.toColorInt())
                                    val text = when (genre) {
                                        is GenreAge -> (genre.age ?: 0).toString() + "+"
                                        is GenreYear -> (genre.year ?: 2026).toString()
                                        is GenreSezon -> stringResource(
                                            (genre.sezon ?: YearSezon.WINTER).displayNameId
                                        )

                                        is GenreEpisodes -> (if (episodes is ListState.Loaded) (episodes as ListState.Loaded<*>).episodes.size else 0).toString()
                                        else -> stringResource(genre.displayNameId)
                                    }

                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = color,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .border(
                                                MaterialTheme.dimens.strokeThin,
                                                color,
                                                MaterialTheme.shapes.small
                                            )
                                            .padding(
                                                horizontal = MaterialTheme.spacing.medium,
                                                vertical = MaterialTheme.spacing.small
                                            )
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    tick()
                                    val eps = episodes
                                    if (eps is ListState.Loaded && continueWith.first != -1) {
                                        val key = ResultKeys.getEpisodesPlayerClosedKey()
                                        playerClosedKey = key
                                        onOpenEpisode(eps.episodes, continueWith.first, key)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (continueWith.second) MaterialTheme.colorScheme.surfaceVariant.copy(
                                        0.38f
                                    ) else LocalCustomColors.current.closeButton,
                                    contentColor = if (continueWith.second) MaterialTheme.colorScheme.onSurfaceVariant else LocalCustomColors.current.onCloseButton
                                )
                            ) {
                                Icon(
                                    imageVector = PlayArrowFilledIco,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Text(
                                    text = stringResource(if (continueWith.second) R.string.startWithFirstEpisode else R.string.continueWatching),
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(start = ButtonDefaults.IconSpacing)
                                )
                            }

                            FilledTonalIconButton(
                                onClick = {
                                    tick()
                                    onFavorite()
                                },
                                colors = IconButtonDefaults.iconButtonColors().copy(
                                    containerColor = if (isInFavourite.first && isInFavourite.second) LocalCustomColors.current.closeButton else MaterialTheme.colorScheme.surfaceVariant.copy(
                                        0.38f
                                    ),
                                    contentColor = if (isInFavourite.first && isInFavourite.second) LocalCustomColors.current.onCloseButton else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                                    imageVector = StarIco,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }

            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        isRefreshing = isRefreshing,
                        state = pullState,
                        // у локальных карточек нечего обновлять из сети
                        enabled = card.anilibriaCardId != null,
                        onRefresh = {
                            tick()
                            isInfoRefreshing = true
                            isContentRefreshing = true
                            updater = !updater
                        }
                    ),
                contentPadding = PaddingValues(
                    start = horizontal + leftInset,
                    end = horizontal + rightInset,
                    top = topInset + horizontal + MaterialTheme.spacing.extraLarge,
                    bottom = bottomInset + horizontal
                ),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    if (isBannerIsSingle) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            banner()
                            info(
                                Modifier
                                    .fillMaxWidth(),
                                Modifier
                            )
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                            banner()
                            info(
                                Modifier
                                    .weight(1f, fill = false)
                                    .widthIn(minInputWidth, maxInputWidth)
                                    .heightIn(max = bannerHeight),
                                Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = card.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .visible(!isSearching),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Row(Modifier.fillMaxWidth()) {
                                    listOf(
                                        AnimePageUiState.Episodes to R.string.episodes,
                                        AnimePageUiState.Linked to R.string.seasons
                                    ).forEach { (state, label) ->
                                        Text(
                                            text = stringResource(label),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (uiState == state) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(MaterialTheme.shapes.small)
                                                .clickable {
                                                    tick()
                                                    query = ""
                                                    uiState = state
                                                }
                                                .padding(vertical = 12.dp)
                                        )
                                    }
                                }

                                val bias by animateFloatAsState(
                                    targetValue = if (uiState == AnimePageUiState.Episodes) -1f else 1f,
                                )
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth(0.5f)
                                            .fillMaxHeight()
                                            .align(BiasAlignment(bias, 0f))
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }

                            IconButton(onClick = {
                                tick()
                                isSearching = true
                            }) {
                                Icon(
                                    imageVector = SearchIco,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                )
                            }

                            Box {
                                IconButton(onClick = {
                                    tick()
                                    isViewMenuOpened = true
                                }) {
                                    Icon(
                                        imageVector = MoreVertIco,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                    )
                                }

                                DropDownMenuWithBlur(
                                    expanded = { isViewMenuOpened },
                                    onDismissRequest = { isViewMenuOpened = false },
                                    hazeState = LocalHazeLayers.current.mainScreen
                                ) {
                                    if (uiState == AnimePageUiState.Episodes) {
                                        PopupMenuItem(
                                            text = stringResource(
                                                R.string.changeView
                                            ),
                                            textColor = MaterialTheme.colorScheme.onSurface,
                                            onClick = {
                                                tick()
                                                viewMode =
                                                    if (viewMode == EpisodesViewMode.Grid) EpisodesViewMode.List
                                                    else EpisodesViewMode.Grid
                                                isViewMenuOpened = false
                                            },
                                            icon = {}
                                        )
                                    } else {
                                        PopupMenuItem(
                                            text = stringResource(R.string.nothingIsHere),
                                            textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.38f
                                            ),
                                            onClick = {},
                                            icon = {}
                                        )
                                    }
                                }
                            }
                        }

                        if (isSearching) {
                            Row(
                                modifier = Modifier
                                    .matchParentSize()
                                    .blockGestures(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = query,
                                    onValueChange = {
                                        query =
                                            if (uiState == AnimePageUiState.Episodes) it.filter(Char::isDigit) else it
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .focusRequester(searchFocus),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = if (uiState == AnimePageUiState.Episodes) KeyboardType.Number
                                        else KeyboardType.Text,
                                        imeAction = ImeAction.Search
                                    ),
                                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                    decorationBox = { inner ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                                .padding(horizontal = 16.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (query.isEmpty()) {
                                                Text(
                                                    text = stringResource(
                                                        if (uiState == AnimePageUiState.Episodes) R.string.searchByEpisodeNumber
                                                        else R.string.searchByName
                                                    ),
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            inner()
                                        }
                                    }
                                )
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    tick()
                                    query = ""
                                    isSearching = false
                                }) {
                                    Icon(
                                        imageVector = CloseIco,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState == AnimePageUiState.Episodes) {
                    when (val state = episodes) {
                        is ListState.Loading -> item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = MaterialTheme.spacing.extraLarge),
                                contentAlignment = Alignment.Center
                            ) { ShapesLoadingIndicator() }
                        }

                        is ListState.Loaded -> if (viewMode == EpisodesViewMode.Grid) {
                            items(state.episodes) { episode ->
                                val preview = episode.preview
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(EpisodeAspectRatio)
                                        .clip(MaterialTheme.shapes.large)
                                        .clickable {
                                            tick()
                                            val key = ResultKeys.getEpisodesPlayerClosedKey()
                                            playerClosedKey = key
                                            onOpenEpisode(state.episodes, episode.position, key)
                                        }
                                ) {
                                    AppAsyncImage(
                                        imageData = preview,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .blur(
                                                radius = if (episode.watched) 0.dp else EpisodeUnwatchedBlurRadius
                                            )
                                    )

                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                                        ) {
                                            StrokeText(
                                                text = stringResource(R.string.Episode) + " ${episode.position}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(start = MaterialTheme.spacing.medium)
                                            )

                                            if (episode.name.isNotBlank()) {
                                                StrokeText(
                                                    text = episode.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.padding(start = MaterialTheme.spacing.medium)
                                                )
                                            }
                                        }


                                        CardWatchedProgressLine(
                                            modifier = Modifier.fillMaxWidth(),
                                            progress = { if (episode.watched) 1f else episode.positionPr },
                                        )
                                    }
                                }
                            }
                        } else {
                            items(state.episodes) { episode ->
                                Box(
                                    Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .clickable {
                                            tick()
                                            val key = ResultKeys.getEpisodesPlayerClosedKey()
                                            playerClosedKey = key
                                            onOpenEpisode(state.episodes, episode.position, key)
                                        }
                                ) { EpisodePreview(episode) }
                            }
                        }
                    }
                } else if (visibleGroups.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (webGroupsObjects is ListState.Loading) {
                                ShapesLoadingIndicator()
                            } else {
                                Text(
                                    text = stringResource(R.string.nothingIsHere),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(visibleGroups) { anime ->
                        Box(
                            Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    tick()
                                    onOpenCard(anime)
                                }
                        ) { CardPreview(card = anime, isSupportDragging = false) }
                    }
                }
            }

            ShapesPullToRefreshIndicator(
                state = pullState,
                isRefreshing = isRefreshing,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = topInset)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        top = topInset + MaterialTheme.spacing.screenHorizontal,
                        start = MaterialTheme.spacing.screenHorizontal + leftInset
                    )
            ) {
                CloseButton(
                    onClick = {
                        focusManager.clearFocus()
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        closePage()
                    }
                )
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
                            text = stringResource(R.string.AboutPage),
                            textColor = MaterialTheme.colorScheme.onSurface,
                            onClick = {
                                tick()
                                isExtraButtonsMenuOpened.value = false
                                isAboutOpened = true
                            },
                            icon = {}
                        )
                    }
                }
            }

            if (isAboutOpened) {
                HelpDialog(
                    title = stringResource(R.string.AboutPage),
                    message = stringResource(R.string.animePageAbout),
                    onDismiss = { isAboutOpened = false }
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ShapesLoadingIndicator()
        }
    }
}

@Composable
fun EpisodePreview(episode: EpisodeInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.flatGridItemHeight)
            .padding(vertical = MaterialTheme.spacing.screenHorizontal / 2)
            .padding(start = MaterialTheme.spacing.screenHorizontal / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        val width =
            (MaterialTheme.dimens.flatGridItemHeight - MaterialTheme.spacing.screenHorizontal) * EpisodeAspectRatio

        Box(
            modifier = Modifier
                .width(width)
                .aspectRatio(EpisodeAspectRatio)
                .clip(MaterialTheme.shapes.small)
        ) {
            AppAsyncImage(
                imageData = episode.preview,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radius = if (episode.watched) 0.dp else EpisodeUnwatchedBlurRadius
                    )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            StrokeText(
                text = stringResource(R.string.Episode) + " ${episode.position}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (episode.name.isNotBlank()) {
                StrokeText(
                    text = episode.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun rememberAccentColors(image: ImageData): List<Color> {
    val context = LocalContext.current

    return produceState(emptyList(), image) {
        val model: Any = when (image) {
            is ImageData.Url -> image.url
            is ImageData.Device -> File(image.path)
            is ImageData.Resource -> return@produceState
        }
        val request = ImageRequest.Builder(context)
            .data(model)
            .size(32)
            .allowHardware(false)
            .build()

        val bitmap = (context.imageLoader.execute(request) as? SuccessResult)
            ?.drawable?.toBitmap() ?: return@produceState

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val hsv = FloatArray(3)
        val weights = FloatArray(12)
        val sums = Array(12) { FloatArray(3) }
        pixels.forEach { pixel ->
            AndroidColor.colorToHSV(pixel, hsv)
            val bucket = (hsv[0] / 30f).toInt().coerceIn(0, 11)
            val weight = hsv[1] * hsv[2]
            weights[bucket] += weight
            sums[bucket][0] += AndroidColor.red(pixel) * weight
            sums[bucket][1] += AndroidColor.green(pixel) * weight
            sums[bucket][2] += AndroidColor.blue(pixel) * weight
        }

        value = weights.indices
            .filter { weights[it] > 0f }
            .sortedByDescending { weights[it] }
            .take(2)
            .map {
                Color(
                    sums[it][0] / weights[it] / 255f,
                    sums[it][1] / weights[it] / 255f,
                    sums[it][2] / weights[it] / 255f
                )
            }
    }.value
}

private fun List<EpisodeInfo>.continueWith(): Int {
    if (this.all { it.watched } || this.all { !it.watched }) return min(this.map { it.position })
    if (this.isEmpty()) return -1
    return this.last { !it.watched }.position
}

private fun ListState<EpisodeInfo>.continueWith(): Pair<Int, Boolean> {
    val eps = this
    return if (eps is ListState.Loaded) {
        if (eps.episodes.isEmpty()) {
            Pair(-1, true)
        } else {
            Pair(
                eps.episodes.continueWith(),
                ((eps.episodes.first { it.position == eps.episodes.continueWith() }.watched) && (eps.episodes.continueWith() == min(
                    eps.episodes.map { it.position })))
            )
        }
    } else {
        Pair(1, true)
    }
}
