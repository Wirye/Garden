package com.example.garden.ui.screens

import android.view.WindowManager
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.garden.GlobalSearchState
import com.example.garden.Layer
import com.example.garden.OverLayLayer
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.database.entities.CardSize
import com.example.garden.database.entities.CarouselType
import com.example.garden.database.entities.CollectionType
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.LayoutType
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.PageType
import com.example.garden.database.entities.PlayListType
import com.example.garden.database.entities.SizeType
import com.example.garden.ui.components.CardChoice
import com.example.garden.ui.components.CarouselChildsEdit
import com.example.garden.ui.components.MainPageBottomBar
import com.example.garden.ui.components.MainPageTopBar
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.components.icons.FolderIco
import com.example.garden.ui.theme.LocalWindowInfo
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.availableCardTypes
import com.example.garden.ui.utils.hazeSourcesForUpperLayers
import com.example.garden.ui.utils.toLayerCreateCardPage
import com.example.garden.ui.utils.toLayerCreateCarouselPage
import com.example.garden.ui.utils.toObjectData
import com.example.garden.utils.toLongId
import com.example.garden.viewmodel.AuthViewModel
import com.example.garden.viewmodel.LayersViewModel
import com.example.garden.viewmodel.MainViewModel
import com.example.garden.viewmodel.RecentQueriesViewModel
import com.example.garden.viewmodel.ResultSenderViewModel
import com.example.garden.viewmodel.SearchViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HazeLayers(
    val mainScreen: HazeState = HazeState(),
)

class HazeStates(
    val hazeStates: List<HazeState> = listOf(HazeState())
)

val LocalHazeLayers = compositionLocalOf { HazeLayers() }
val LocalHazeStates = compositionLocalOf { HazeStates() }


@Composable
fun MainScreen(
    layersViewModel: LayersViewModel,
    resultSenderViewModel: ResultSenderViewModel,
    authViewModel: AuthViewModel,
    recentQueriesViewModel: RecentQueriesViewModel,
    searchViewModel: SearchViewModel
) {
    val backStack = layersViewModel.backStack
    val mainPagesBackStack = layersViewModel.mainPagesStack
    val hazeLayers = remember { HazeLayers() }

    val hazeStates = remember(backStack.size) {
        mutableStateOf(List(backStack.size) { HazeState() })
    }

    CompositionLocalProvider(LocalHazeStates provides HazeStates(hazeStates.value)) {
        CompositionLocalProvider(LocalHazeLayers provides hazeLayers) {

            val bottomBarHeight = remember { mutableStateOf(0.dp) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeLayers.mainScreen)
            ) {
                backStack.forEachIndexed { index, layer ->
                    key(layer.id) {
                        val isTopLayer = index == backStack.lastIndex
                        val canGoBack =
                            ((backStack.firstOrNull { it !is Layer.MainPage } != null) || (mainPagesBackStack.size > 1))
                        LayerScreen(
                            layer = layer,
                            isTopLayer = isTopLayer,
                            isOverlay = layer is Layer.OverLay,
                            useBlurForDimming = layer is Layer.OverLay,
                            closeLayer = {
                                var close = true

                                if (layer is Layer.OverLay) {
                                    if (layer.info is OverLayLayer.GlobalSearch) {
                                        if (layer.info.state == GlobalSearchState.Found) {
                                            close = false
                                            layer.info.state = GlobalSearchState.Searching
                                            resultSenderViewModel.sendResult(
                                                ResultKeys.GLOBAL_SEARCH_STATE_CHANGED_TO_SEARCHING,
                                                ResultKeys.NO_DATA
                                            )
                                        }
                                    }
                                }

                                if (close) {
                                    if (!isTopLayer) {
                                        layersViewModel.removeLayerById(layer.id)
                                    } else {
                                        layersViewModel.popLayer()
                                    }
                                }
                            },
                            canGoBack = canGoBack,
                            isRootLayer = layer is Layer.MainPage,
                            modifier = Modifier
                                .hazeSourcesForUpperLayers(
                                    LocalHazeStates.current.hazeStates,
                                    index
                                ),
                            bottomBarHeight = bottomBarHeight.value,
                            layerIndex = index,
                            layersViewModel = layersViewModel,
                            resultSenderViewModel = resultSenderViewModel,
                            authViewModel = authViewModel,
                            recentQueriesViewModel = recentQueriesViewModel,
                            searchViewModel = searchViewModel
                        )
                    }
                }
                val bs = backStack.last()
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    AnimatedVisibility(
                        visible = backStack.last() is Layer.MainPage,
                        enter = fadeIn(tween(300)) + slideInVertically(
                            animationSpec = tween(300),
                            initialOffsetY = { it / 2 }
                        ),
                        exit = fadeOut(tween(300)) + slideOutVertically(
                            animationSpec = tween(300),
                            targetOffsetY = { it / 2 }
                        )
                    ) {
                        MainPageBottomBar(
                            active = backStack.last() is Layer.MainPage,
                            heightState = { bottomBarHeight.value = it },
                            {
                                val bsLast = backStack.last()
                                if (bsLast is Layer.MainPage) {
                                    if (bsLast.page != PageType.Home) {
                                        layersViewModel.openLayer(
                                            Layer.MainPage(PageType.Home, mutableMapOf(), 0)
                                        )
                                    }
                                }
                            },
                            {
                                val bsLast = backStack.last()
                                if (bsLast is Layer.MainPage) {
                                    if (bsLast.page != PageType.Anime) {
                                        layersViewModel.openLayer(
                                            Layer.MainPage(PageType.Anime, mutableMapOf(), 0)
                                        )
                                    }
                                }
                            },
                            {
                                val bsLast = backStack.last()
                                if (bsLast is Layer.MainPage) {
                                    if (bsLast.page != PageType.Music) {
                                        layersViewModel.openLayer(
                                            Layer.MainPage(PageType.Music, mutableMapOf(), 0)
                                        )
                                    }
                                }
                            },
                            {
                                val bsLast = backStack.last()
                                if (bsLast is Layer.MainPage) {
                                    if (bsLast.page != PageType.Manga) {
                                        layersViewModel.openLayer(
                                            Layer.MainPage(PageType.Manga, mutableMapOf(), 0)
                                        )
                                    }
                                }
                            },
                            pageState = if (bs is Layer.MainPage) bs.page else PageType.Home
                        )
                    }
                }
            }
        }
    }
}

val LocalLayerIndex = compositionLocalOf { 0 }

@Composable
private fun LayerContent(
    modifier: Modifier = Modifier,
    layersViewModel: LayersViewModel,
    layer: Layer,
    closeLayer: () -> Unit,
    isTopLayer: Boolean,
    bottomBarHeight: Dp,
    layerIndex: Int,
    resultSenderViewModel: ResultSenderViewModel,
    authViewModel: AuthViewModel,
    recentQueriesViewModel: RecentQueriesViewModel,
    searchViewModel: SearchViewModel
) {
    val mainViewModel: MainViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    CompositionLocalProvider(LocalLayerIndex provides layerIndex) {
        Box(modifier = modifier.fillMaxSize()) {
            val haptic = LocalHapticFeedback.current

            var deleteAuthorState by rememberSaveable { mutableStateOf(Triple(false, "", -1L)) }
            if (deleteAuthorState.first && deleteAuthorState.third != -1L) {
                AlertDialog(
                    onDismissRequest = { deleteAuthorState = Triple(false, "", -1L) },
                    title = {
                        Text(text = stringResource(R.string.DeleteAuthor) + " ${deleteAuthorState.second}?")
                    },
                    text = {
                        Text(text = stringResource(R.string.DeleteAuthorWarning))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                coroutineScope.launch {
                                    mainViewModel.deleteObjectById(deleteAuthorState.third)
                                    deleteAuthorState = Triple(false, "", -1L)
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                deleteAuthorState = Triple(false, "", -1L)
                            }
                        ) {
                            Text(text = stringResource(R.string.Cancel))
                        }
                    }
                )
            }

            when (layer) {
                is Layer.MainPage -> {
                    val topBarHeightState = remember { mutableStateOf(0.dp) }

                    var editingCarouselId by remember { mutableLongStateOf(-1L) }

                    var isEditCarouselChildsExpanded by rememberSaveable { mutableStateOf(false) }
                    if (isEditCarouselChildsExpanded && editingCarouselId != -1L) {
                        CarouselChildsEdit(
                            id = editingCarouselId,
                            mainViewModel = mainViewModel,
                            onDismiss = { isEditCarouselChildsExpanded = false },
                            onSave = {
                                coroutineScope.launch {
                                    mainViewModel.updatePositions(it)
                                }
                            }
                        )
                    }

                    MainPageTopBar(
                        offsetPx = { 0f },
                        active = false,
                        heightState = { topBarHeightState.value = it },
                        onSearch = {},
                        onSettings = {},
                        onAddCarousel = {},
                        onEdit = {},
                        onSavedPage = {}
                    )

                    var isCardChoiceExpandedState by rememberSaveable {
                        mutableStateOf(
                            Triple(
                                -1L,
                                false,
                                CarouselType.Anime
                            )
                        )
                    }
                    if (isCardChoiceExpandedState.second && isCardChoiceExpandedState.first != -1L) {
                        CardChoice(
                            searchViewModel = searchViewModel,
                            isSingleChoice = true,
                            cardTypes = isCardChoiceExpandedState.third.availableCardTypes(),
                            initCardsList = emptyList(),
                            onDismiss = {
                                isCardChoiceExpandedState = Triple(-1L, false, CarouselType.Anime)
                            },
                            onApply = {
                                coroutineScope.launch {
                                    mainViewModel.insertObjectWithLinkInsert(
                                        parentId = isCardChoiceExpandedState.first,
                                        targetId = it.first()
                                    )
                                }
                            }
                        )
                    }

                    var isCreateCardChoiceExpandedState by rememberSaveable {
                        mutableStateOf(
                            Triple(
                                -1L,
                                false,
                                CarouselType.Anime
                            )
                        )
                    }
                    Box {
                        CreateCardChoice(
                            expanded = isCreateCardChoiceExpandedState.second,
                            hazeState = LocalHazeLayers.current.mainScreen,
                            onDismiss = {
                                isCreateCardChoiceExpandedState = Triple(
                                    isCreateCardChoiceExpandedState.first,
                                    false,
                                    isCreateCardChoiceExpandedState.third
                                )
                            },
                            onAddCard = {
                                if (isCreateCardChoiceExpandedState.first != -1L) {
                                    layersViewModel.openLayer(
                                        Layer.CreateCardPage(
                                            parentId = isCreateCardChoiceExpandedState.first,
                                            name = "",
                                            description = "",
                                            authorsList = listOf(),
                                            genreList = emptyList(),
                                            episodesList = emptyList(),
                                            cardType = isCreateCardChoiceExpandedState.third.availableCardTypes()
                                                .firstOrNull() ?: ElementType.AnimeCard,
                                            chaptersList = emptyList(),
                                            cardsList = emptyList(),
                                            carouselType = isCreateCardChoiceExpandedState.third,
                                            playlistType = PlayListType.Music,
                                        )
                                    )
                                }
                                isCreateCardChoiceExpandedState = Triple(
                                    isCreateCardChoiceExpandedState.first,
                                    false,
                                    isCreateCardChoiceExpandedState.third
                                )
                            },
                            onPickCard = {
                                isCardChoiceExpandedState = Triple(
                                    isCreateCardChoiceExpandedState.first,
                                    true,
                                    isCreateCardChoiceExpandedState.third
                                )
                                isCreateCardChoiceExpandedState =
                                    Triple(-1L, false, isCreateCardChoiceExpandedState.third)
                            }
                        )
                    }

                    MainPage(
                        isSupportCreating = true,
                        viewModel = mainViewModel,
                        layer = layer,
                        isTopLayer = isTopLayer,
                        topBarHeight = topBarHeightState.value,
                        bottomBarHeight = bottomBarHeight,
                        openSettings = {
                            layersViewModel.openLayer(Layer.AppSettings())
                        },
                        openCreateCardPage = { parentId, carouselType ->
                            isCreateCardChoiceExpandedState =
                                Triple(parentId.toLongId(), true, carouselType)
                        },
                        openCreateCarouselPage = {
                            layersViewModel.openLayer(
                                Layer.CreateCarouselPage(
                                    name = "",
                                    carouselType = CarouselType.Anime,
                                    layoutType = LayoutType.DEFAULT,
                                    page = layer.page,
                                    childsSize = CardSize.MEDIUM,
                                    carouselCollectionType = CollectionType.None,
                                    childsShowName = true,
                                    childsShowAuthor = true,
                                    childsCornerRadius = SizeType.MEDIUM,
                                    childsNamePosition = 0,
                                    childsShowAlreadyWatchedLine = true,
                                    dovodchik = false,
                                    showDovodchikDots = false,
                                    objectsInOneLine = null,
                                    maxLines = null
                                )
                            )
                        },
                        openEditCarouselPage = {
                            coroutineScope.launch {
                                val info =
                                    mainViewModel.getRootObjectById(it.id.toLongId()) as? ObjectData.Carousel
                                        ?: return@launch
                                layersViewModel.openLayer(
                                    info.toLayerCreateCarouselPage(page = layer.page)
                                )
                            }
                        },
                        openEditCardPage = { data, parentId, carouselType ->
                            coroutineScope.launch {
                                val info =
                                    mainViewModel.getRootObjectById(data.id.toLongId()) as? ObjectData.Card
                                        ?: return@launch
                                layersViewModel.openLayer(
                                    info.toLayerCreateCardPage(
                                        parentId = parentId.toLongId(),
                                        carouselType = carouselType
                                    )
                                )
                            }
                        },
                        openCarouselChildsEdit = {
                            editingCarouselId = it.toLongId()
                            isEditCarouselChildsExpanded = true
                        },
                        deleteCard = { id ->
                            coroutineScope.launch {
                                val card = mainViewModel.getRootObjectById(id.toLongId())
                                if (card is ObjectData.Card.Artist) {
                                    deleteAuthorState = Triple(true, card.name, id.toLongId())
                                } else {
                                    mainViewModel.deleteObjectById(id.toLongId())
                                }
                            }
                        },
                        deleteCarousel = { id ->
                            coroutineScope.launch {
                                mainViewModel.deleteObjectById(id.toLongId())
                            }
                        },
                        openGlobalSearch = {
                            layersViewModel.openLayer(
                                layer = Layer.OverLay(
                                    info = OverLayLayer.GlobalSearch(
                                        state = GlobalSearchState.Searching
                                    )
                                )
                            )
                        },
                        openSavedPage = {
                            layersViewModel.openLayer(layer = Layer.UnspecifiedPage())
                        }
                    )
                }

                is Layer.AnimePage -> {}

                is Layer.VideoPlayer -> {}

                is Layer.OverLay -> {
                    when (layer.info) {
                        is OverLayLayer.GlobalSearch -> {
                            GlobalSearch(
                                closeLayer = closeLayer,
                                recentQueriesViewModel = recentQueriesViewModel,
                                searchViewModel = searchViewModel,
                                resultSenderViewModel = resultSenderViewModel,
                                layer = layer,
                                onCardClick = {}
                            )
                        }
                    }
                }

                is Layer.CreateCardPage -> {
                    val coroutineScope = rememberCoroutineScope()

                    CreateCardPage(
                        searchViewModel = searchViewModel,
                        layer = layer,
                        layersViewModel = layersViewModel,
                        resultSenderViewModel = resultSenderViewModel,
                        onClose = { closeLayer() },
                        onCloseAndApply = { objData, layer ->
                            coroutineScope.launch {
                                mainViewModel.saveObject(objData, PageType.Home, layer.parentId)
                            }
                            closeLayer()
                        }
                    )
                }

                is Layer.PageWithSearch -> {
                    PageWithSearch(
                        layer = layer,
                        resultSenderViewModel = resultSenderViewModel,
                        layersViewModel = layersViewModel,
                        onClose = { closeLayer() }
                    )
                }

                is Layer.CreateCarouselPage -> {
                    val mainViewModel: MainViewModel = viewModel()
                    val coroutineScope = rememberCoroutineScope()

                    CreateCarouselPage(
                        layer = layer,
                        onClose = { closeLayer() },
                        onSaveAndClose = { info ->
                            coroutineScope.launch {
                                val data = info.toObjectData()
                                mainViewModel.saveObject(data, page = layer.page, parentId = null)
                            }
                            closeLayer()
                        }
                    )
                }

                is Layer.AppSettings -> {
                    AppSettings(
                        authViewModel = authViewModel,
                        layersViewModel = layersViewModel,
                        onClose = closeLayer
                    )
                }

                is Layer.AniLibertyLoginPage -> {
                    AniLibertyLoginPage(
                        modifier = Modifier.fillMaxSize(),
                        authViewModel = authViewModel,
                        onClose = closeLayer
                    )
                }

                is Layer.UnspecifiedPage -> {
                    val coroutineScope = rememberCoroutineScope()
                    UnspecifiedPageScreen(
                        mainViewModel = mainViewModel,
                        layer = layer,
                        closeLayer = closeLayer,
                        onClickCard = {},
                        deleteCard = { id ->
                            coroutineScope.launch {
                                val card = mainViewModel.getRootObjectById(id.toLongId())
                                if (card is ObjectData.Card.Artist) {
                                    deleteAuthorState = Triple(true, card.name, id.toLongId())
                                } else {
                                    mainViewModel.deleteObjectById(id.toLongId())
                                }
                            }
                        },
                        openEditCardPage = { data ->
                            coroutineScope.launch {
                                val info =
                                    mainViewModel.getRootObjectById(data.id.toLongId()) as? ObjectData.Card
                                        ?: return@launch
                                layersViewModel.openLayer(
                                    info.toLayerCreateCardPage(
                                        parentId = 1L,
                                        carouselType = CarouselType.Anime
                                    )
                                )
                            }
                        },
                        openCreateCardPage = {
                            layersViewModel.openLayer(
                                Layer.CreateCardPage(
                                    parentId = 1L,
                                    name = "",
                                    description = "",
                                    authorsList = listOf(),
                                    genreList = emptyList(),
                                    episodesList = emptyList(),
                                    cardType = ElementType.AnimeCard,
                                    chaptersList = emptyList(),
                                    cardsList = emptyList(),
                                    carouselType = CarouselType.All,
                                    playlistType = PlayListType.Music,
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun LayerScreen(
    modifier: Modifier = Modifier,
    layer: Layer,
    bottomBarHeight: Dp,
    layerIndex: Int,
    layersViewModel: LayersViewModel,
    isTopLayer: Boolean,
    isOverlay: Boolean,
    isRootLayer: Boolean,
    canGoBack: Boolean,
    useBlurForDimming: Boolean = false,
    closeLayer: () -> Unit,
    resultSenderViewModel: ResultSenderViewModel,
    authViewModel: AuthViewModel,
    recentQueriesViewModel: RecentQueriesViewModel,
    searchViewModel: SearchViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalWindowInfo.current.widthDp.toPx() }

    val isSwipeFromLeft = remember { Animatable(1f) }

    val shouldAnimateEnter = isTopLayer && !isOverlay && !isRootLayer

    val scale = remember { Animatable(if (shouldAnimateEnter) 0.85f else 1.0f) }
    val translationX = remember { Animatable(if (shouldAnimateEnter) screenWidthPx else 0f) }
    val overlayContentAlpha = remember { Animatable(if (isOverlay) 0f else 1f) }

    val dimAlpha = remember { Animatable(0f) }

    LaunchedEffect(isTopLayer) {
        if (isTopLayer && !isRootLayer) {
            dimAlpha.animateTo(0.5f, tween(250))
        }
    }

    LaunchedEffect(isTopLayer) {
        if (isTopLayer && !isRootLayer) {
            if (isOverlay) {
                overlayContentAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 200, delayMillis = 100)
                )
            } else {
                launch {
                    translationX.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
                }
                launch {
                    scale.animateTo(
                        1.0f,
                        tween(300, delayMillis = 100, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val close = {
        coroutineScope.launch {
            if (isOverlay && !(layer is Layer.OverLay && layer.info is OverLayLayer.GlobalSearch && layer.info.state == GlobalSearchState.Found)) {
                launch {
                    overlayContentAlpha.animateTo(0f, tween(150))
                }
                launch {
                    dimAlpha.animateTo(0f, tween(durationMillis = 75, delayMillis = 75))
                }
                delay(150.milliseconds)
                closeLayer()
            } else if (!isRootLayer && !(layer is Layer.OverLay && layer.info is OverLayLayer.GlobalSearch && layer.info.state == GlobalSearchState.Found)) {
                val isQuickSwipe = scale.value > 0.95f

                if (isQuickSwipe) {
                    launch {
                        scale.animateTo(0.9f, tween(100))
                    }
                    launch {
                        translationX.animateTo(
                            targetValue = screenWidthPx,
                            animationSpec = tween(
                                durationMillis = 200,
                                delayMillis = 100,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                    launch {
                        dimAlpha.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = 200,
                                delayMillis = 100
                            )
                        )
                    }
                    delay(300.milliseconds)
                } else {
                    launch {
                        translationX.animateTo(
                            targetValue = screenWidthPx,
                            animationSpec = tween(
                                durationMillis = 200,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                    launch {
                        dimAlpha.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(durationMillis = 200)
                        )
                    }
                    delay(200.milliseconds)
                }
                closeLayer()
            } else {
                closeLayer()
            }
        }
    }

    DisposableEffect(isTopLayer, canGoBack) {
        if (!isTopLayer || !canGoBack) {
            onDispose {}
        } else {
            val callback = object : OnBackPressedCallback(true) {

                override fun handleOnBackStarted(backEvent: BackEventCompat) {
                    val isLeft = backEvent.touchX <= (screenWidthPx / 2f)

                    coroutineScope.launch {
                        isSwipeFromLeft.snapTo(if (isLeft) 1f else -1f)
                    }
                }

                override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                    val progress = backEvent.progress
                    val direction = isSwipeFromLeft.value

                    coroutineScope.launch {
                        if (isOverlay && !(layer is Layer.OverLay && layer.info is OverLayLayer.GlobalSearch && layer.info.state == GlobalSearchState.Found)) {
                            overlayContentAlpha.snapTo(1.0f - progress)
                        } else if (!isRootLayer && !(layer is Layer.OverLay && layer.info is OverLayLayer.GlobalSearch && layer.info.state == GlobalSearchState.Found)) {
                            val currentScale = 1.0f - (progress * 0.15f)
                            val currentTranslation = direction * progress * (screenWidthPx * 0.25f)
                            scale.snapTo(currentScale)
                            translationX.snapTo(currentTranslation)
                            dimAlpha.snapTo(0.5f * (1.0f - progress))
                        }
                    }
                }

                override fun handleOnBackPressed() {
                    close()
                }

                override fun handleOnBackCancelled() {
                    coroutineScope.launch { scale.animateTo(1.0f, tween(150)) }
                    coroutineScope.launch { translationX.animateTo(0f, tween(150)) }
                    coroutineScope.launch { overlayContentAlpha.animateTo(1.0f, tween(150)) }
                    coroutineScope.launch { dimAlpha.animateTo(0.5f, tween(150)) }
                }
            }

            backDispatcher?.addCallback(callback)

            onDispose {
                callback.remove()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isTopLayer && !isRootLayer && dimAlpha.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (useBlurForDimming) {
                            Modifier
                                .background(Color.Black.copy(alpha = dimAlpha.value))
                                .hazeEffect(
                                    LocalHazeStates.current.hazeStates[layerIndex],
                                    style = HazeMaterials.thin()
                                )
                        } else {
                            Modifier.background(Color.Black.copy(alpha = dimAlpha.value))
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        closeLayer()
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.translationX = translationX.value
                    alpha = if (isOverlay) overlayContentAlpha.value else 1f
                }
        ) {
            LayerContent(
                layer = layer,
                closeLayer = { close() },
                isTopLayer = isTopLayer,
                bottomBarHeight = bottomBarHeight,
                layerIndex = layerIndex,
                layersViewModel = layersViewModel,
                resultSenderViewModel = resultSenderViewModel,
                authViewModel = authViewModel,
                recentQueriesViewModel = recentQueriesViewModel,
                searchViewModel = searchViewModel
            )
        }
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun CreateCardChoice(
    expanded: Boolean,
    hazeState: HazeState,
    onDismiss: () -> Unit,
    onAddCard: () -> Unit,
    onPickCard: () -> Unit
) {
    val density = LocalDensity.current
    val topInsetPx = WindowInsets.safeDrawing.getTop(density)
    val rightInsetPx = WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current)
    val leftInsetPx = WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current)
    val bottomInsetPx = WindowInsets.safeDrawing.getBottom(density)

    val additionalPadding = MaterialTheme.spacing.screenHorizontal

    val topInset = with(density) { topInsetPx.toDp() } + additionalPadding
    val rightInset = with(density) { rightInsetPx.toDp() } + additionalPadding
    val leftInset = with(density) { leftInsetPx.toDp() } + additionalPadding
    val bottomInset = with(density) { bottomInsetPx.toDp() } + additionalPadding

    val transitionState = remember { MutableTransitionState(expanded) }

    LaunchedEffect(expanded) {
        transitionState.targetState = expanded
    }

    val transition = rememberTransition(transitionState, label = "enter/out")

    val dialogAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 250) },
        label = "dialogAlpha"
    ) { state ->
        if (state) 1f else 0f
    }

    val contentAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 250, delayMillis = 100) },
        label = "contentAlpha"
    ) { state ->
        if (state) 1f else 0f
    }

    if (expanded || transitionState.currentState) {
        val isDarkTheme = isSystemInDarkTheme()
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                decorFitsSystemWindows = false
            )
        ) {
            val haptic = LocalHapticFeedback.current

            val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
            SideEffect {
                dialogWindow?.let { window ->
                    window.setWindowAnimations(0)
                    window.setDimAmount(0f)
                    window.setBackgroundBlurRadius(0)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = !isDarkTheme
                    controller.isAppearanceLightNavigationBars = !isDarkTheme
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onDismiss)
                    .graphicsLayer {
                        this.alpha = dialogAlpha
                    }
                    .hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.thin()
                    )
                    .padding(
                        top = topInset,
                        bottom = bottomInset,
                        start = leftInset,
                        end = rightInset
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            this.alpha = contentAlpha
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onAddCard()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                        ) {
                            Icon(
                                imageVector = AddIco,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                            )
                        }

                        Text(
                            text = stringResource(R.string.Create),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                onPickCard()
                                onDismiss()
                            },
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(MaterialTheme.spacing.medium)
                        ) {
                            Icon(
                                imageVector = FolderIco,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                            )
                        }

                        Text(
                            text = stringResource(R.string.Pick),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}