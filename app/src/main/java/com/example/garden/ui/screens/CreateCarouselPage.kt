package com.example.garden.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.database.CardSize
import com.example.garden.database.CarouselType
import com.example.garden.database.CollectionType
import com.example.garden.database.ImageData
import com.example.garden.database.LayoutType
import com.example.garden.database.SizeType
import com.example.garden.ui.components.AsyncImageWithAddPlaceholder
import com.example.garden.ui.components.SelectableDropDownMenuWithBlur
import com.example.garden.ui.components.SmartFilePicker
import com.example.garden.ui.components.rememberFilePicker
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.ui.utils.dataForModel
import com.example.garden.ui.utils.getAllCollectionByCarouselType
import com.example.garden.ui.utils.hazeSourcesForUpperLayers
import com.example.garden.viewmodel.CreateCarouselViewModel
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCarouselPage(
    layer: Layer.CreateCarouselPage,
    onClose: () -> Unit,
    onSaveAndClose: (Layer.CreateCarouselPage) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val stateViewModel: CreateCarouselViewModel = viewModel(
        key = layer.id.toString(), factory = CreateCarouselViewModel.provideFactory(layer)
    )

    LaunchedEffect(layer, stateViewModel) {
        stateViewModel.onUpdate = {
            layer.name = it.name
            layer.carouselType = it.carouselType
            layer.showIco = it.showIco
            layer.ico = it.ico
            layer.layoutType = it.layoutType
            layer.showDovodchikDots = it.showDovodchikDots
            layer.dovodchik = it.dovodchik
            layer.adaptiveGridSize = it.adaptiveGridSize
            layer.maxLines = it.maxLines
            layer.maxLinesForAdaptiveSize = it.maxLinesForAdaptiveSize
            layer.maxObjectsInOneLineForAdaptiveSize = it.maxObjectsInOneLineForAdaptiveSize
            layer.childsCornerRadius = it.childsCornerRadius
            layer.childsShowName = it.childsShowName
            layer.childsNamePosition = it.childsNamePosition
            layer.childsShowAlreadyWatchedLine = it.childsShowAlreadyWatchedLine
            layer.childsSize = it.childsSize
            layer.childsShowAuthor = it.childsShowAuthor
            layer.carouselCollectionType = it.carouselCollectionType
            layer.objectsInOneLine = it.objectsInOneLine
        }
    }

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
    val spacing = MaterialTheme.spacing.medium

    val openIcoImagePicker = rememberFilePicker(
        mimeTypes = arrayOf("image/*")
    ) { uri ->
        if (uri != null) {
            stateViewModel.update {
                copy(
                    ico = ImageData.Device(uri.toString())
                )
            }
        }
    }

    LaunchedEffect(
        stateViewModel.state.layoutType,
        stateViewModel.state.dovodchik,
        stateViewModel.state.showDovodchikDots,
        stateViewModel.state.carouselCollectionType
    ) {
        if (stateViewModel.state.layoutType == LayoutType.CAROUSEL_FROM_GRID && !stateViewModel.state.dovodchik) {
            stateViewModel.update {
                copy(
                    dovodchik = true
                )
            }
        }

        if (stateViewModel.state.layoutType == LayoutType.CAROUSEL_GRID && (stateViewModel.state.dovodchik || stateViewModel.state.showDovodchikDots)) {
            stateViewModel.update {
                copy(
                    dovodchik = false,
                    showDovodchikDots = false
                )
            }
        }

        if (stateViewModel.state.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID) {
            stateViewModel.update {
                copy(
                    adaptiveGridSize = false,
                    maxLinesForAdaptiveSize = null,
                    maxObjectsInOneLineForAdaptiveSize = null,
                    objectsInOneLine = 1
                )
            }
        }

        if (stateViewModel.state.carouselCollectionType == null) {
            stateViewModel.update {
                copy(
                    carouselCollectionType = CollectionType.None
                )
            }
        }
    }

    val isIcoChangeDialogExpanded = remember { mutableStateOf(false) }

    SmartFilePicker(
        expanded = isIcoChangeDialogExpanded.value,
        onDismiss = { isIcoChangeDialogExpanded.value = false },
        onFileDelete = {
            isIcoChangeDialogExpanded.value = false
            stateViewModel.update {
                copy(
                    ico = null
                )
            }
        },
        onFileChange = {
            isIcoChangeDialogExpanded.value = false
            openIcoImagePicker()
        },
        hazeState = LocalHazeLayers.current.mainScreen,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .blockGestures()
            .clearFocus(focusManager)
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
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    focusManager.clearFocus()
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

            val isCarouselTypeSelectMenuOpened = remember { mutableStateOf(false) }
            Box(
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Row(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                focusManager.clearFocus()
                                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                isCarouselTypeSelectMenuOpened.value = true
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.creatingCarousel),
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

                val availableCarouselTypes = remember(stateViewModel.state.carouselType) {
                    mutableStateOf(CarouselType.entries.toList())
                }

                val selectedIndex =
                    remember(stateViewModel.state.carouselType, availableCarouselTypes) {
                        {
                            if (availableCarouselTypes.value.indexOf(stateViewModel.state.carouselType) != -1) {
                                availableCarouselTypes.value.indexOf(stateViewModel.state.carouselType)
                            } else {
                                0
                            }
                        }
                    }

                SelectableDropDownMenuWithBlur(
                    expanded = { isCarouselTypeSelectMenuOpened.value },
                    onDismissRequest = { isCarouselTypeSelectMenuOpened.value = false },
                    hazeState = LocalHazeLayers.current.mainScreen,
                    selectedIndex = selectedIndex(),
                    onSelect = {
                        haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                        stateViewModel.update {
                            copy(carouselType = availableCarouselTypes.value[it])
                        }
                    },
                    items = availableCarouselTypes.value.map { stringResource(it.displayNameId) }
                )
            }

            Box(
                modifier = Modifier.size(MaterialTheme.dimens.minButtonHeight)
            )
        }

        val pagerState = rememberPagerState(pageCount = { 2 })

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            VerticalPager(
                state = pagerState,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = 0.1f
                ),
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(end = MaterialTheme.spacing.medium)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                    ) {
                        when (page) {
                            0 -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .border(
                                            width = MaterialTheme.dimens.strokeThick,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.extraLarge
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (
                                        !stateViewModel.objectsInOneLineError
                                        && !stateViewModel.objectsInOneLineZeroError && !stateViewModel.maxLinesZeroError
                                        && !stateViewModel.maxObjectsInOneLineForAdaptiveGridSizeZeroError &&
                                        !stateViewModel.maxLinesForAdaptiveGridSizeZeroError &&
                                        !stateViewModel.layoutTypeError
                                    ) {
                                        CarouselPreview(info = stateViewModel.state)
                                    } else {
                                        Text(
                                            text = stringResource(R.string.error),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                        )
                                    }

                                }
                            }

                            1 -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clip(MaterialTheme.shapes.extraLarge)
                                        .border(
                                            width = MaterialTheme.dimens.strokeThick,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceContainer)
                                            .padding(horizontal = MaterialTheme.spacing.screenHorizontal)
                                    ) {
                                        val listState = rememberLazyListState()
                                        LazyColumn(
                                            modifier = Modifier.padding(
                                                bottom = (bottomInset - MaterialTheme.spacing.large - MaterialTheme.dimens.minButtonHeight).coerceAtLeast(
                                                    0.dp
                                                )
                                            ),
                                            state = listState,
                                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                                            contentPadding = PaddingValues(
                                                vertical = MaterialTheme.spacing.screenHorizontal
                                            )
                                        ) {
                                            item("name") {
                                                var name by rememberSaveable(stateViewModel.state.name) { mutableStateOf(stateViewModel.state.name) }

                                                OutlinedTextField(
                                                    value = name,
                                                    onValueChange = {
                                                        name = it
                                                        stateViewModel.update {
                                                            copy(
                                                                name = name
                                                            )
                                                        }
                                                    },
                                                    shape = MaterialTheme.shapes.medium,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                                    ),
                                                    placeholder = {
                                                        Text(
                                                            text = stringResource(R.string.title),
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    },
                                                    singleLine = true,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }

                                            item("ico") {
                                                val expanded = stateViewModel.state.showIco

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .clipToBounds()
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    if (stateViewModel.state.showIco) {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOff
                                                                        )
                                                                    } else {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOn
                                                                        )
                                                                    }
                                                                    stateViewModel.update {
                                                                        copy(
                                                                            showIco = !showIco
                                                                        )
                                                                    }
                                                                }
                                                                .padding(MaterialTheme.spacing.medium),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                        ) {
                                                            Text(
                                                                modifier = Modifier.weight(
                                                                    1f,
                                                                    fill = false
                                                                ),
                                                                text = stringResource(R.string.showingIco),
                                                                style = MaterialTheme.typography.titleMedium,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                overflow = TextOverflow.Ellipsis
                                                            )

                                                            Switch(
                                                                checked = expanded,
                                                                onCheckedChange = {
                                                                    if (it) {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOn
                                                                        )
                                                                    } else {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOff
                                                                        )
                                                                    }
                                                                    stateViewModel.update {
                                                                        copy(
                                                                            showIco = it
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        }

                                                        AnimatedVisibility(
                                                            visible = expanded,
                                                            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                                                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(
                                                                        start = 16.dp,
                                                                        end = 16.dp,
                                                                        bottom = 16.dp
                                                                    ),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    modifier = Modifier.weight(
                                                                        1f,
                                                                        fill = false
                                                                    ),
                                                                    text = stringResource(R.string.ico),
                                                                    style = MaterialTheme.typography.titleMedium,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )

                                                                val shape =
                                                                    MaterialTheme.shapes.medium
                                                                AsyncImageWithAddPlaceholder(
                                                                    modifier = Modifier
                                                                        .size(
                                                                            MaterialTheme.dimens.minButtonHeight
                                                                        )
                                                                        .clip(shape)
                                                                        .clickable(
                                                                            onClick = {
                                                                                haptic.performHapticFeedback(
                                                                                    HapticFeedbackType.ContextClick
                                                                                )
                                                                                isIcoChangeDialogExpanded.value =
                                                                                    true
                                                                            }
                                                                        )
                                                                        .hazeSource(LocalHazeLayers.current.mainScreen)
                                                                        .hazeSourcesForUpperLayers(
                                                                            LocalHazeStates.current.hazeStates,
                                                                            LocalLayerIndex.current
                                                                        ),
                                                                    shape = shape,
                                                                    model = stateViewModel.state.ico?.dataForModel()
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            item("preset") {
                                                var isBottomSheetOpen by rememberSaveable { mutableStateOf(false) }

                                                if (isBottomSheetOpen) {
                                                    CarouselCollectionPicker(
                                                        onDismiss = { isBottomSheetOpen = false },
                                                        carouselType = stateViewModel.state.carouselType,
                                                        initCollectionType = stateViewModel.state.carouselCollectionType,
                                                        onApply = { namee, collection ->
                                                            stateViewModel.update {
                                                                copy(
                                                                    carouselCollectionType = collection
                                                                )
                                                            }
                                                            if (collection != null) {
                                                                if (collection.overrideEnabled) {
                                                                    stateViewModel.update {
                                                                        copy(
                                                                            name = namee,
                                                                            ico = collection.overrideIco,
                                                                            showIco = collection.overrideShowIco,
                                                                            dovodchik = collection.overrideDovodchik,
                                                                            showDovodchikDots = collection.overrideShowDovodchikDots,
                                                                            maxLines = collection.overrideMaxLines,
                                                                            objectsInOneLine = collection.overrideObjectsInOneLine,
                                                                            adaptiveGridSize = collection.overrideAdaptiveGridSize,
                                                                            maxLinesForAdaptiveSize = collection.overrideMaxLinesForAdaptiveSize,
                                                                            maxObjectsInOneLineForAdaptiveSize = collection.overrideMaxObjectsInOneLineForAdaptiveSize,
                                                                            childsCornerRadius = collection.overrideChildsCornerRadius,
                                                                            childsShowName = collection.overrideChildsShowName,
                                                                            childsNamePosition = collection.overrideChildsNamePosition,
                                                                            childsShowAlreadyWatchedLine = collection.overrideChildsShowAlreadyWatchedLine,
                                                                            childsSize = collection.overrideChildsSize,
                                                                            childsShowAuthor = collection.overrideChildsShowAuthor,
                                                                            layoutType = collection.overrideLayoutType
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                        .clickable(
                                                            onClick = {
                                                                isBottomSheetOpen = true
                                                                haptic.performHapticFeedback(
                                                                    HapticFeedbackType.ContextClick
                                                                )
                                                            }
                                                        )
                                                        .padding(MaterialTheme.spacing.medium)
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        modifier = Modifier.weight(
                                                            1f,
                                                            fill = false
                                                        ),
                                                        text = stringResource(R.string.presets),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    Icon(
                                                        painter = painterResource(R.drawable.chevron_forward),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            item("layoutType") {
                                                val options = listOf(
                                                    stringResource(R.string.default_),
                                                    stringResource(R.string.grid),
                                                    stringResource(R.string.fromGrids)
                                                )

                                                val selectedIndex =
                                                    when (stateViewModel.state.layoutType) {
                                                        LayoutType.DEFAULT -> 0
                                                        LayoutType.CAROUSEL_GRID -> 1
                                                        LayoutType.CAROUSEL_FROM_GRID, LayoutType.CAROUSEL_FROM_FLAT_GRID -> 2
                                                        else -> 0
                                                    }

                                                val expanded =
                                                    selectedIndex == 1 || selectedIndex == 2

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .clipToBounds()
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        val disabledAlpha = 0.6f
                                                        SingleChoiceSegmentedButtonRow(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(MaterialTheme.spacing.medium)
                                                        ) {
                                                            options.forEachIndexed { index, label ->
                                                                SegmentedButton(
                                                                    shape = SegmentedButtonDefaults.itemShape(
                                                                        index = index,
                                                                        count = options.size
                                                                    ),
                                                                    colors = SegmentedButtonDefaults.colors()
                                                                        .copy(
                                                                            activeContainerColor = MaterialTheme.colorScheme.primary,
                                                                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                                            inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                                                                            inactiveBorderColor = MaterialTheme.colorScheme.outline,
                                                                            activeBorderColor = MaterialTheme.colorScheme.primary,
                                                                            disabledInactiveBorderColor = MaterialTheme.colorScheme.outline.copy(
                                                                                alpha = disabledAlpha
                                                                            ),
                                                                            disabledInactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                                                                                alpha = disabledAlpha
                                                                            ),
                                                                            disabledInactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(
                                                                                alpha = disabledAlpha
                                                                            ),
                                                                            disabledActiveContainerColor = MaterialTheme.colorScheme.primary.copy(
                                                                                alpha = disabledAlpha
                                                                            ),
                                                                            disabledActiveContentColor = MaterialTheme.colorScheme.onPrimary.copy(
                                                                                alpha = disabledAlpha
                                                                            ),
                                                                            disabledActiveBorderColor = MaterialTheme.colorScheme.primary.copy(
                                                                                alpha = disabledAlpha
                                                                            )
                                                                        ),
                                                                    onClick = {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.VirtualKey
                                                                        )
                                                                        stateViewModel.update {
                                                                            copy(
                                                                                layoutType = when (index) {
                                                                                    0 -> LayoutType.DEFAULT
                                                                                    1 -> LayoutType.CAROUSEL_GRID
                                                                                    2 -> if (layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID) LayoutType.CAROUSEL_FROM_FLAT_GRID else LayoutType.CAROUSEL_FROM_GRID
                                                                                    else -> LayoutType.DEFAULT
                                                                                }
                                                                            )
                                                                        }
                                                                    },
                                                                    selected = index == selectedIndex
                                                                ) {
                                                                    Text(
                                                                        text = label,
                                                                        style = MaterialTheme.typography.bodyMedium,
                                                                        maxLines = 1,
                                                                        overflow = TextOverflow.Ellipsis
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        AnimatedVisibility(
                                                            visible = stateViewModel.state.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID || stateViewModel.state.layoutType == LayoutType.CAROUSEL_FROM_GRID,
                                                            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                                                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clickable {
                                                                        if (stateViewModel.state.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID) {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOff
                                                                            )
                                                                        } else {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOn
                                                                            )
                                                                        }
                                                                        stateViewModel.update {
                                                                            copy(
                                                                                layoutType = if (stateViewModel.state.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID)
                                                                                    LayoutType.CAROUSEL_FROM_GRID else
                                                                                    LayoutType.CAROUSEL_FROM_FLAT_GRID
                                                                            )
                                                                        }
                                                                    }
                                                                    .padding(MaterialTheme.spacing.medium),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    modifier = Modifier.weight(
                                                                        1f,
                                                                        fill = false
                                                                    ),
                                                                    text = stringResource(R.string.flatGrid),
                                                                    style = MaterialTheme.typography.titleMedium,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )

                                                                Switch(
                                                                    checked = stateViewModel.state.layoutType == LayoutType.CAROUSEL_FROM_FLAT_GRID,
                                                                    onCheckedChange = {
                                                                        if (it) {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOn
                                                                            )
                                                                        } else {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOff
                                                                            )
                                                                        }
                                                                        stateViewModel.update {
                                                                            copy(
                                                                                layoutType = if (it) LayoutType.CAROUSEL_FROM_FLAT_GRID
                                                                                else LayoutType.CAROUSEL_FROM_GRID
                                                                            )
                                                                        }
                                                                    }
                                                                )
                                                            }
                                                        }

                                                        AnimatedVisibility(
                                                            visible = expanded,
                                                            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                                                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                                                        ) {
                                                            Column(
                                                                modifier = Modifier.fillMaxWidth()
                                                            ) {
                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .padding(MaterialTheme.spacing.medium),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.SpaceAround
                                                                ) {
                                                                    var maxLines by rememberSaveable(
                                                                        stateViewModel.state.maxLines
                                                                    ) {
                                                                        mutableStateOf(
                                                                            stateViewModel.state.maxLines?.toString()
                                                                                ?: ""
                                                                        )
                                                                    }
                                                                    var objectsInOneLine by rememberSaveable(
                                                                        stateViewModel.state.objectsInOneLine
                                                                    ) {
                                                                        mutableStateOf(
                                                                            stateViewModel.state.objectsInOneLine?.toString()
                                                                                ?: ""
                                                                        )
                                                                    }

                                                                    OutlinedTextField(
                                                                        value = maxLines,
                                                                        onValueChange = {
                                                                            maxLines = it
                                                                            stateViewModel.update {
                                                                                copy(
                                                                                    maxLines = if (maxLines.isEmpty()) null else maxLines.toInt()
                                                                                )
                                                                            }
                                                                        },
                                                                        shape = MaterialTheme.shapes.medium,
                                                                        colors = OutlinedTextFieldDefaults.colors(
                                                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                                                        ),
                                                                        placeholder = {
                                                                            Text(
                                                                                text = stringResource(
                                                                                    R.string.rows
                                                                                ),
                                                                                style = MaterialTheme.typography.bodyMedium
                                                                            )
                                                                        },
                                                                        isError = stateViewModel.maxLinesZeroError,
                                                                        supportingText = {
                                                                            if (stateViewModel.maxLinesZeroError) {
                                                                                Text(
                                                                                    text = stringResource(
                                                                                        R.string.mustBeUpperThenZero
                                                                                    ),
                                                                                    style = MaterialTheme.typography.bodySmall,
                                                                                    color = MaterialTheme.colorScheme.error
                                                                                )
                                                                            }
                                                                        },
                                                                        keyboardOptions = KeyboardOptions(
                                                                            keyboardType = KeyboardType.Number
                                                                        ),
                                                                        singleLine = true,
                                                                        modifier = Modifier.weight(
                                                                            1f,
                                                                            fill = false
                                                                        )
                                                                    )

                                                                    Spacer(
                                                                        modifier = Modifier.size(
                                                                            MaterialTheme.spacing.extraSmall
                                                                        )
                                                                    )

                                                                    OutlinedTextField(
                                                                        enabled = stateViewModel.state.layoutType != LayoutType.CAROUSEL_FROM_FLAT_GRID,
                                                                        value = objectsInOneLine,
                                                                        onValueChange = {
                                                                            objectsInOneLine = it
                                                                            stateViewModel.update {
                                                                                copy(
                                                                                    objectsInOneLine = if (objectsInOneLine.isEmpty()) null else objectsInOneLine.toInt()
                                                                                )
                                                                            }
                                                                        },
                                                                        shape = MaterialTheme.shapes.medium,
                                                                        colors = OutlinedTextFieldDefaults.colors(
                                                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                                                        ),
                                                                        placeholder = {
                                                                            Text(
                                                                                text = stringResource(
                                                                                    R.string.columns
                                                                                ),
                                                                                style = MaterialTheme.typography.bodyMedium
                                                                            )
                                                                        },
                                                                        isError = stateViewModel.objectsInOneLineZeroError,
                                                                        supportingText = {
                                                                            if (stateViewModel.objectsInOneLineZeroError) {
                                                                                Text(
                                                                                    text = stringResource(
                                                                                        R.string.mustBeUpperThenZero
                                                                                    ),
                                                                                    style = MaterialTheme.typography.bodySmall,
                                                                                    color = MaterialTheme.colorScheme.error
                                                                                )
                                                                            }
                                                                        },
                                                                        keyboardOptions = KeyboardOptions(
                                                                            keyboardType = KeyboardType.Number
                                                                        ),
                                                                        singleLine = true,
                                                                        modifier = Modifier.weight(
                                                                            1f,
                                                                            fill = false
                                                                        )
                                                                    )
                                                                }

                                                                Row(
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .clickable {
                                                                            if (stateViewModel.state.layoutType != LayoutType.CAROUSEL_FROM_FLAT_GRID) {
                                                                                if (stateViewModel.state.adaptiveGridSize) {
                                                                                    haptic.performHapticFeedback(
                                                                                        HapticFeedbackType.ToggleOff
                                                                                    )
                                                                                } else {
                                                                                    haptic.performHapticFeedback(
                                                                                        HapticFeedbackType.ToggleOn
                                                                                    )
                                                                                }
                                                                                stateViewModel.update {
                                                                                    copy(
                                                                                        adaptiveGridSize = !adaptiveGridSize
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                        .padding(MaterialTheme.spacing.medium)
                                                                        .padding(top = 0.dp),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                                ) {
                                                                    Text(
                                                                        text = stringResource(R.string.adaptiveSize),
                                                                        style = MaterialTheme.typography.titleMedium,
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                    )

                                                                    Switch(
                                                                        enabled = stateViewModel.state.layoutType != LayoutType.CAROUSEL_FROM_FLAT_GRID,
                                                                        checked = stateViewModel.state.adaptiveGridSize,
                                                                        onCheckedChange = {
                                                                            if (it) {
                                                                                haptic.performHapticFeedback(
                                                                                    HapticFeedbackType.ToggleOn
                                                                                )
                                                                            } else {
                                                                                haptic.performHapticFeedback(
                                                                                    HapticFeedbackType.ToggleOff
                                                                                )
                                                                            }
                                                                            stateViewModel.update {
                                                                                copy(
                                                                                    adaptiveGridSize = it
                                                                                )
                                                                            }
                                                                        }
                                                                    )
                                                                }

                                                                val adaptiveGridSizeExpanded =
                                                                    stateViewModel.state.adaptiveGridSize

                                                                AnimatedVisibility(
                                                                    visible = adaptiveGridSizeExpanded,
                                                                    enter = expandVertically(
                                                                        expandFrom = Alignment.Top
                                                                    ) + fadeIn(),
                                                                    exit = shrinkVertically(
                                                                        shrinkTowards = Alignment.Top
                                                                    ) + fadeOut()
                                                                ) {
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(MaterialTheme.spacing.medium)
                                                                            .padding(top = 0.dp),
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                        horizontalArrangement = Arrangement.SpaceAround
                                                                    ) {
                                                                        var maxLinesForAdaptiveGridSize by rememberSaveable(
                                                                            stateViewModel.state.maxLinesForAdaptiveSize
                                                                        ) {
                                                                            mutableStateOf(
                                                                                stateViewModel.state.maxLinesForAdaptiveSize?.toString()
                                                                                    ?: ""
                                                                            )
                                                                        }
                                                                        var maxObjectsInOneLineForAdaptiveSize by rememberSaveable(
                                                                            stateViewModel.state.maxObjectsInOneLineForAdaptiveSize
                                                                        ) {
                                                                            mutableStateOf(
                                                                                stateViewModel.state.maxObjectsInOneLineForAdaptiveSize?.toString()
                                                                                    ?: ""
                                                                            )
                                                                        }

                                                                        OutlinedTextField(
                                                                            enabled = stateViewModel.state.layoutType != LayoutType.CAROUSEL_FROM_FLAT_GRID,
                                                                            value = maxLinesForAdaptiveGridSize,
                                                                            onValueChange = {
                                                                                maxLinesForAdaptiveGridSize =
                                                                                    it
                                                                                stateViewModel.update {
                                                                                    copy(
                                                                                        maxLinesForAdaptiveSize = if (maxLinesForAdaptiveGridSize.isEmpty()) null else maxLinesForAdaptiveGridSize.toInt()
                                                                                    )
                                                                                }
                                                                            },
                                                                            shape = MaterialTheme.shapes.medium,
                                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                                                            ),
                                                                            placeholder = {
                                                                                Text(
                                                                                    text = stringResource(
                                                                                        R.string.maxRows
                                                                                    ),
                                                                                    style = MaterialTheme.typography.bodyMedium
                                                                                )
                                                                            },
                                                                            isError = stateViewModel.maxLinesForAdaptiveGridSizeZeroError,
                                                                            supportingText = {
                                                                                if (stateViewModel.maxLinesForAdaptiveGridSizeZeroError) {
                                                                                    Text(
                                                                                        text = stringResource(
                                                                                            R.string.mustBeUpperThenZero
                                                                                        ),
                                                                                        style = MaterialTheme.typography.bodySmall,
                                                                                        color = MaterialTheme.colorScheme.error
                                                                                    )
                                                                                }
                                                                            },
                                                                            keyboardOptions = KeyboardOptions(
                                                                                keyboardType = KeyboardType.Number
                                                                            ),
                                                                            singleLine = true,
                                                                            modifier = Modifier.weight(
                                                                                1f,
                                                                                fill = false
                                                                            )
                                                                        )

                                                                        Spacer(
                                                                            modifier = Modifier.size(
                                                                                MaterialTheme.spacing.extraSmall
                                                                            )
                                                                        )

                                                                        OutlinedTextField(
                                                                            enabled = stateViewModel.state.layoutType != LayoutType.CAROUSEL_FROM_FLAT_GRID,
                                                                            value = maxObjectsInOneLineForAdaptiveSize,
                                                                            onValueChange = {
                                                                                maxObjectsInOneLineForAdaptiveSize =
                                                                                    it
                                                                                stateViewModel.update {
                                                                                    copy(
                                                                                        maxObjectsInOneLineForAdaptiveSize = if (maxObjectsInOneLineForAdaptiveSize.isEmpty()) null else maxObjectsInOneLineForAdaptiveSize.toInt()
                                                                                    )
                                                                                }
                                                                            },
                                                                            shape = MaterialTheme.shapes.medium,
                                                                            colors = OutlinedTextFieldDefaults.colors(
                                                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                                                            ),
                                                                            placeholder = {
                                                                                Text(
                                                                                    text = stringResource(
                                                                                        R.string.maxColumns
                                                                                    ),
                                                                                    style = MaterialTheme.typography.bodyMedium
                                                                                )
                                                                            },
                                                                            keyboardOptions = KeyboardOptions(
                                                                                keyboardType = KeyboardType.Number
                                                                            ),
                                                                            isError = stateViewModel.objectsInOneLineError || stateViewModel.maxObjectsInOneLineForAdaptiveGridSizeZeroError,
                                                                            supportingText = {
                                                                                if (stateViewModel.objectsInOneLineError) {
                                                                                    Text(
                                                                                        text = stringResource(
                                                                                            R.string.lowerThenLimit
                                                                                        ),
                                                                                        style = MaterialTheme.typography.bodySmall,
                                                                                        color = MaterialTheme.colorScheme.error
                                                                                    )
                                                                                } else if (stateViewModel.maxObjectsInOneLineForAdaptiveGridSizeZeroError) {
                                                                                    Text(
                                                                                        text = stringResource(
                                                                                            R.string.mustBeUpperThenZero
                                                                                        ),
                                                                                        style = MaterialTheme.typography.bodySmall,
                                                                                        color = MaterialTheme.colorScheme.error
                                                                                    )
                                                                                }
                                                                            },
                                                                            singleLine = true,
                                                                            modifier = Modifier.weight(
                                                                                1f,
                                                                                fill = false
                                                                            )
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            item("cardSize") {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                        .padding(MaterialTheme.spacing.medium)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.cardSize),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    val options = listOf(
                                                        CardSize.SMALL,
                                                        CardSize.MEDIUM,
                                                        CardSize.LARGE
                                                    )

                                                    val steps = options.size - 1

                                                    var sliderValue by rememberSaveable(stateViewModel.state.childsSize) {
                                                        mutableIntStateOf(
                                                            options.indexOf(stateViewModel.state.childsSize)
                                                        )
                                                    }

                                                    Slider(
                                                        value = sliderValue.toFloat(),
                                                        onValueChange = {
                                                            haptic.performHapticFeedback(
                                                                HapticFeedbackType.SegmentTick
                                                            )
                                                            sliderValue = it.toInt()
                                                            stateViewModel.update {
                                                                copy(
                                                                    childsSize = options[it.toInt()]
                                                                )
                                                            }
                                                        },
                                                        valueRange = 0f..steps.toFloat(),
                                                        steps = steps - 1,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                            }

                                            item("cardsShowName") {
                                                val expanded = stateViewModel.state.childsShowName

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .clipToBounds()
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    if (stateViewModel.state.childsShowName) {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOff
                                                                        )
                                                                    } else {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOn
                                                                        )
                                                                    }
                                                                    stateViewModel.update {
                                                                        copy(
                                                                            childsShowName = !childsShowName
                                                                        )
                                                                    }
                                                                }
                                                                .padding(MaterialTheme.spacing.medium),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                        ) {
                                                            Text(
                                                                modifier = Modifier.weight(
                                                                    1f,
                                                                    fill = false
                                                                ),
                                                                text = stringResource(R.string.showingName),
                                                                style = MaterialTheme.typography.titleMedium,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )

                                                            Switch(
                                                                checked = expanded,
                                                                onCheckedChange = {
                                                                    if (it) {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOn
                                                                        )
                                                                    } else {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOff
                                                                        )
                                                                    }
                                                                    stateViewModel.update {
                                                                        copy(
                                                                            childsShowName = it
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        }

                                                        AnimatedVisibility(
                                                            visible = expanded,
                                                            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                                                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                                                        ) {
                                                            val options = listOf(
                                                                stringResource(R.string.outside),
                                                                stringResource(R.string.inside)
                                                            )

                                                            val selectedIndex =
                                                                when (stateViewModel.state.childsNamePosition) {
                                                                    0 -> 0
                                                                    null, 1 -> 1
                                                                    else -> 1
                                                                }

                                                            val disabledAlpha = 0.6f
                                                            SingleChoiceSegmentedButtonRow(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(MaterialTheme.spacing.medium)
                                                                    .padding(top = 0.dp)
                                                            ) {
                                                                options.forEachIndexed { index, label ->
                                                                    SegmentedButton(
                                                                        shape = SegmentedButtonDefaults.itemShape(
                                                                            index = index,
                                                                            count = options.size
                                                                        ),
                                                                        colors = SegmentedButtonDefaults.colors()
                                                                            .copy(
                                                                                activeContainerColor = MaterialTheme.colorScheme.primary,
                                                                                activeContentColor = MaterialTheme.colorScheme.onPrimary,
                                                                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                                                inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                                                                                inactiveBorderColor = MaterialTheme.colorScheme.outline,
                                                                                activeBorderColor = MaterialTheme.colorScheme.primary,
                                                                                disabledInactiveBorderColor = MaterialTheme.colorScheme.outline.copy(
                                                                                    alpha = disabledAlpha
                                                                                ),
                                                                                disabledInactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                                                                                    alpha = disabledAlpha
                                                                                ),
                                                                                disabledInactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(
                                                                                    alpha = disabledAlpha
                                                                                ),
                                                                                disabledActiveContainerColor = MaterialTheme.colorScheme.primary.copy(
                                                                                    alpha = disabledAlpha
                                                                                ),
                                                                                disabledActiveContentColor = MaterialTheme.colorScheme.onPrimary.copy(
                                                                                    alpha = disabledAlpha
                                                                                ),
                                                                                disabledActiveBorderColor = MaterialTheme.colorScheme.primary.copy(
                                                                                    alpha = disabledAlpha
                                                                                )
                                                                            ),
                                                                        onClick = {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.VirtualKey
                                                                            )
                                                                            stateViewModel.update {
                                                                                copy(
                                                                                    childsNamePosition = index
                                                                                )
                                                                            }
                                                                        },
                                                                        selected = index == selectedIndex
                                                                    ) {
                                                                        Text(
                                                                            text = label,
                                                                            style = MaterialTheme.typography.bodyMedium,
                                                                            maxLines = 1,
                                                                            overflow = TextOverflow.Ellipsis
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            item("cardCornerRadius") {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                        .padding(MaterialTheme.spacing.medium)
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.cardCornerRadius),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    val options = listOf(
                                                        null,
                                                        SizeType.SMALL,
                                                        SizeType.MEDIUM,
                                                        SizeType.LARGE,
                                                        SizeType.XLARGE
                                                    )

                                                    val steps = options.size - 1

                                                    var sliderValue by rememberSaveable(stateViewModel.state.childsCornerRadius) {
                                                        mutableIntStateOf(
                                                            options.indexOf(stateViewModel.state.childsCornerRadius)
                                                        )
                                                    }

                                                    Slider(
                                                        value = sliderValue.toFloat(),
                                                        onValueChange = {
                                                            haptic.performHapticFeedback(
                                                                HapticFeedbackType.SegmentTick
                                                            )
                                                            sliderValue = it.toInt()
                                                            stateViewModel.update {
                                                                copy(
                                                                    childsCornerRadius = options[it.toInt()]
                                                                )
                                                            }
                                                        },
                                                        valueRange = 0f..steps.toFloat(),
                                                        steps = steps - 1,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                            }

                                            item("cardsShowAlreadyWatchedLine") {
                                                val expanded =
                                                    stateViewModel.state.childsShowAlreadyWatchedLine

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .clipToBounds()
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                if (stateViewModel.state.childsShowAlreadyWatchedLine) {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOff
                                                                    )
                                                                } else {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOn
                                                                    )
                                                                }
                                                                stateViewModel.update {
                                                                    copy(
                                                                        childsShowAlreadyWatchedLine = !childsShowAlreadyWatchedLine
                                                                    )
                                                                }
                                                            }
                                                            .padding(MaterialTheme.spacing.medium),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                    ) {
                                                        Text(
                                                            modifier = Modifier.weight(
                                                                1f,
                                                                fill = false
                                                            ),
                                                            text = stringResource(R.string.showingAlreadyWatchedLine),
                                                            style = MaterialTheme.typography.titleMedium,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )

                                                        Switch(
                                                            checked = expanded,
                                                            onCheckedChange = {
                                                                if (it) {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOn
                                                                    )
                                                                } else {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOff
                                                                    )
                                                                }
                                                                stateViewModel.update {
                                                                    copy(
                                                                        childsShowAlreadyWatchedLine = it
                                                                    )
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            item("cardsShowAuthor") {
                                                val expanded = stateViewModel.state.childsShowAuthor

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .clipToBounds()
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                if (stateViewModel.state.childsShowAuthor) {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOff
                                                                    )
                                                                } else {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOn
                                                                    )
                                                                }
                                                                stateViewModel.update {
                                                                    copy(
                                                                        childsShowAuthor = !childsShowAuthor
                                                                    )
                                                                }
                                                            }
                                                            .padding(MaterialTheme.spacing.medium),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                    ) {
                                                        Text(
                                                            modifier = Modifier.weight(
                                                                1f,
                                                                fill = false
                                                            ),
                                                            text = stringResource(R.string.showingAuthor),
                                                            style = MaterialTheme.typography.titleMedium,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            overflow = TextOverflow.Ellipsis
                                                        )

                                                        Switch(
                                                            checked = expanded,
                                                            onCheckedChange = {
                                                                if (it) {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOn
                                                                    )
                                                                } else {
                                                                    haptic.performHapticFeedback(
                                                                        HapticFeedbackType.ToggleOff
                                                                    )
                                                                }
                                                                stateViewModel.update {
                                                                    copy(
                                                                        childsShowAuthor = it
                                                                    )
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            item("dovodchik") {
                                                val expanded = stateViewModel.state.dovodchik

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(MaterialTheme.shapes.medium)
                                                        .clipToBounds()
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                ) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    if (stateViewModel.state.layoutType != LayoutType.CAROUSEL_GRID) {
                                                                        if (stateViewModel.state.dovodchik) {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOff
                                                                            )
                                                                        } else {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOn
                                                                            )
                                                                        }
                                                                        stateViewModel.update {
                                                                            copy(
                                                                                dovodchik = !dovodchik
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                                .padding(MaterialTheme.spacing.medium),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                        ) {
                                                            Text(
                                                                modifier = Modifier.weight(
                                                                    1f,
                                                                    fill = false
                                                                ),
                                                                text = stringResource(R.string.dovodchik),
                                                                style = MaterialTheme.typography.titleMedium,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                overflow = TextOverflow.Ellipsis
                                                            )

                                                            Switch(
                                                                checked = stateViewModel.state.dovodchik,
                                                                enabled = stateViewModel.state.layoutType != LayoutType.CAROUSEL_GRID,
                                                                onCheckedChange = {
                                                                    if (it) {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOn
                                                                        )
                                                                    } else {
                                                                        haptic.performHapticFeedback(
                                                                            HapticFeedbackType.ToggleOff
                                                                        )
                                                                    }
                                                                    stateViewModel.update {
                                                                        copy(
                                                                            dovodchik = it
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        }

                                                        AnimatedVisibility(
                                                            visible = expanded,
                                                            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                                                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                                                        ) {
                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clickable {
                                                                        if (stateViewModel.state.layoutType != LayoutType.CAROUSEL_GRID) {
                                                                            if (stateViewModel.state.showDovodchikDots) {
                                                                                haptic.performHapticFeedback(
                                                                                    HapticFeedbackType.ToggleOff
                                                                                )
                                                                            } else {
                                                                                haptic.performHapticFeedback(
                                                                                    HapticFeedbackType.ToggleOn
                                                                                )
                                                                            }
                                                                            stateViewModel.update {
                                                                                copy(
                                                                                    showDovodchikDots = !showDovodchikDots
                                                                                )
                                                                            }
                                                                        }
                                                                    }
                                                                    .padding(MaterialTheme.spacing.medium)
                                                                    .padding(top = 0.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    modifier = Modifier.weight(
                                                                        1f,
                                                                        fill = false
                                                                    ),
                                                                    text = stringResource(R.string.showingDovodchikDots),
                                                                    style = MaterialTheme.typography.titleMedium,
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )

                                                                Switch(
                                                                    checked = stateViewModel.state.showDovodchikDots,
                                                                    enabled = stateViewModel.state.layoutType != LayoutType.CAROUSEL_GRID,
                                                                    onCheckedChange = {
                                                                        if (it) {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOn
                                                                            )
                                                                        } else {
                                                                            haptic.performHapticFeedback(
                                                                                HapticFeedbackType.ToggleOff
                                                                            )
                                                                        }
                                                                        stateViewModel.update {
                                                                            copy(
                                                                                showDovodchikDots = it
                                                                            )
                                                                        }
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            item("addCarousel") {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            haptic.performHapticFeedback(
                                                                HapticFeedbackType.ContextClick
                                                            )
                                                            focusManager.clearFocus()
                                                            onSaveAndClose(stateViewModel.state)
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
                                                                modifier = Modifier.size(
                                                                    MaterialTheme.dimens.iconLarge
                                                                ),
                                                                contentDescription = null
                                                            )
                                                            Text(
                                                                text = stringResource(R.string.addCarousel),
                                                                style = MaterialTheme.typography.labelLarge,
                                                                modifier = Modifier.weight(
                                                                    1f,
                                                                    fill = false
                                                                ),
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(vertical = MaterialTheme.spacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.dotSpacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val isSelected = pagerState.currentPage == iteration

                    val color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }

                    Box(
                        modifier = Modifier
                            .width(MaterialTheme.dimens.dotSize)
                            .height(if (isSelected) MaterialTheme.dimens.activeDotWidth else MaterialTheme.dimens.dotSize)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarouselCollectionPicker(
    onDismiss: () -> Unit,
    carouselType: CarouselType,
    initCollectionType: CollectionType?,
    onApply: (String, CollectionType?) -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val animateAndDismiss: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
            }
        }
    }

    var selectedCollection: CollectionType? by rememberSaveable { mutableStateOf(initCollectionType) }

    val collections = remember(carouselType) { getAllCollectionByCarouselType(carouselType) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.screenHorizontal)
                .clearFocus(focusManager)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.selectCollection_action),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.fillMaxWidth().height(MaterialTheme.spacing.medium))

            Column(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .verticalScroll(rememberScrollState())
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    collections.forEach { collection ->
                        val isSelected = collection == selectedCollection

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selectedCollection = collection
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.defaultMinSize(minWidth = MaterialTheme.dimens.minButtonHeight, minHeight = MaterialTheme.dimens.minButtonHeight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(collection.displayIco.dataForModel())
                                            .placeholder(R.drawable.placeholder)
                                            .error(R.drawable.placeholder)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(MaterialTheme.dimens.iconLarge)
                                            .clip(MaterialTheme.shapes.extraSmall)
                                            .hazeSource(LocalHazeLayers.current.mainScreen)
                                            .hazeSourcesForUpperLayers(
                                                LocalHazeStates.current.hazeStates,
                                                LocalLayerIndex.current
                                            )
                                    )
                                }

                                Text(
                                    modifier = Modifier.weight(1f, fill = false),
                                    text = stringResource(collection.displayNameId),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            val name = stringResource(selectedCollection?.overrideNameId ?: R.string.withoutName)

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    animateAndDismiss()
                    onApply(name, selectedCollection)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(R.string.apply),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
