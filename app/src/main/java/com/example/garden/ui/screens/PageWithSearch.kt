package com.example.garden.ui.screens

import android.os.Parcelable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.garden.Layer
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.VoiceInfo
import com.example.garden.ui.components.CloseButton
import com.example.garden.ui.components.DropDownMenuWithBlur
import com.example.garden.ui.components.EpisodeDraft
import com.example.garden.ui.components.EpisodeRow
import com.example.garden.ui.components.PickedFile
import com.example.garden.ui.components.PopupMenuItem
import com.example.garden.ui.components.addEpisodeFiles
import com.example.garden.ui.components.addVariants
import com.example.garden.ui.components.displayName
import com.example.garden.ui.components.duplicatedEpisodeNumbers
import com.example.garden.ui.components.episodesAreValid
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.components.icons.MoreVertIco
import com.example.garden.ui.components.icons.SearchIco
import com.example.garden.ui.components.newDraftId
import com.example.garden.ui.components.rememberMultipleFilesPicker
import com.example.garden.ui.components.removeVariant
import com.example.garden.ui.components.setVoice
import com.example.garden.ui.components.sortEpisodesByNumber
import com.example.garden.ui.components.updateEpisode
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.utils.getMediaDuration
import com.example.garden.utils.search.searchInList
import com.example.garden.viewmodel.LayersViewModel
import com.example.garden.viewmodel.PageWithSearchSaveOutput
import com.example.garden.viewmodel.ResultSenderViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.Locale

@Composable
fun PageWithSearch(
    layer: Layer.PageWithSearch,
    resultSenderViewModel: ResultSenderViewModel,
    layersViewModel: LayersViewModel,
    onClose: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val input = remember { layer.startsInfo }
    val kind = remember { EditorKind.of(input.initType) }

    val inputList = remember {
        input.items
            .filter { kind.accepts(it) }
            .toMutableStateList()
    }

    LaunchedEffect(inputList) {
        snapshotFlow { inputList.toList() }
            .distinctUntilChanged()
            .collect { layer.startsInfo.items = it }
    }

    // ── вложенное редактирование страниц главы ──────────────────────────────
    val pendingKeys =
        remember { mutableListOf<Pair<Long, String>>() }   // (id главы, ключ результата)

    val onEditChapterPages: (ChapterDraft) -> Unit = { chapter ->
        val key = ResultKeys.getPageWithEditSearchKey()
        pendingKeys.add(chapter.id to key)
        layersViewModel.openLayer(
            Layer.PageWithSearch(
                key = key,
                startsInfo = PageWithSearchInput(
                    items = chapter.pages,
                    initType = PageDraft.Empty
                )
            )
        )
    }

    var lastVoice by rememberSaveable { mutableStateOf(VoiceInfo(name = "", isAniLibria = false)) }
    var pendingEpisodeId by rememberSaveable { mutableStateOf<Long?>(null) }   // null = новые эпизоды

    val pickVideos = rememberMultipleFilesPicker(arrayOf("video/*")) { uris ->
        coroutineScope.launch {
            val files = uris.map {
                PickedFile(
                    it.toString(),
                    context.displayName(it),
                    context.getMediaDuration(it.toString())
                )
            }
            val target = pendingEpisodeId
            if (target == null) inputList.addEpisodeFiles(files, lastVoice)
            else inputList.addVariants(target, files, lastVoice)
            pendingEpisodeId = null
        }
    }

    var addedLanguages by rememberSaveable { mutableStateOf(listOf<String>()) }
    var selectedLanguage by rememberSaveable { mutableStateOf<String?>(null) }
    var isAddLanguageDialogShown by rememberSaveable { mutableStateOf(false) }

    val pageLanguages by remember {
        derivedStateOf {
            val existing = inputList.filterIsInstance<PageDraft>()
                .flatMap { page -> page.variants.map { it.language } }
            (existing.ifEmpty { listOf(Locale.getDefault().language) } + addedLanguages).distinct()
        }
    }
    val language = selectedLanguage?.takeIf { it in pageLanguages } ?: pageLanguages.first()

    val pickImages = rememberMultipleFilesPicker(arrayOf("image/*")) { uris ->
        val images = uris
            .map { it to context.displayName(it) }
            .sortedWith { a, b -> naturalCompare(a.second, b.second) }
            .map { ImageData.Device(it.first.toString()) }
        inputList.addPageImages(images, language)
    }

    val onAddItem: () -> Unit = {
        when (kind) {
            EditorKind.Episodes -> {
                pendingEpisodeId = null
                pickVideos()
            }

            EditorKind.Chapters -> inputList.addChapter()
            EditorKind.Pages -> pickImages()
        }
    }

    val duplicates by remember(inputList) {
        derivedStateOf {
            when (kind) {
                EditorKind.Episodes -> inputList.duplicatedEpisodeNumbers()
                EditorKind.Chapters -> inputList.duplicatedChapterNumbers()
                EditorKind.Pages -> emptySet()
            }
        }
    }
    val canSave by remember(inputList) {
        derivedStateOf {
            when (kind) {
                EditorKind.Episodes -> inputList.episodesAreValid()
                EditorKind.Chapters -> inputList.chaptersAreValid()
                EditorKind.Pages -> true
            }
        }
    }

    val searchState = rememberTextFieldState(initialText = "")
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    val reorderableState = rememberReorderableLazyListState(
        listState,
        PaddingValues(0.dp)
    ) { from, to ->
        val fromIndex = inputList.indexOfFirst { it.id == from.key }
        val toIndex = inputList.indexOfFirst { it.id == to.key }
        if (fromIndex != -1 && toIndex != -1) {
            inputList.add(toIndex, inputList.removeAt(fromIndex))
        }
    }

    val headerItemCount =
        if (kind == EditorKind.Pages) 4 else 3   // topBarSpacer, topBarText, [languageBar], spacer

    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.safeDrawing.getTop(density).toDp() }
    val bottomInset = with(density) { WindowInsets.safeDrawing.getBottom(density).toDp() }
    val leftInset = with(density) {
        WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current).toDp()
    }
    val rightInset = with(density) {
        WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current).toDp()
    }

    // Поиск: число прыгает на эпизод/главу/страницу с таким номером, текст ищется по названию
    val searchText = searchState.text.toString()
    val minButtonHeight = MaterialTheme.dimens.minButtonHeight
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            val number = searchText.trim().toIntOrNull()
            val targetIndex = when {
                number != null && kind == EditorKind.Pages -> number - 1
                number != null -> inputList.indexOfFirst {
                    (it as? EpisodeDraft)?.number == number || (it as? ChapterDraft)?.number == number
                }

                else -> {
                    val names = inputList.mapNotNull { it.name }.filter { it.isNotEmpty() }
                    searchInList(names, searchText, minScore = 50).firstOrNull()
                        ?.let { matched -> inputList.indexOfFirst { it.name == matched } }
                        ?: -1
                }
            } + headerItemCount
            coroutineScope.launch {
                listState.requestScrollToItem(
                    index = targetIndex,
                    scrollOffset = -with(density) { (minButtonHeight * 2f + topInset).toPx() }.toInt()
                )
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { layer.firstElementPosition = it }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .blockGestures()
            .clearFocus(focusManager)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                contentPadding = PaddingValues(
                    start = MaterialTheme.spacing.screenHorizontal + leftInset,
                    end = MaterialTheme.spacing.screenHorizontal + rightInset,
                    bottom = MaterialTheme.spacing.extraLarge * 4 + MaterialTheme.spacing.medium + bottomInset
                )
            ) {
                item("topBarSpacer") {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.screenHorizontal + topInset)
                    )
                }

                item("topBarText") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .height(MaterialTheme.dimens.minButtonHeight)
                                .width(MaterialTheme.dimens.minButtonHeight + MaterialTheme.spacing.small)
                        )

                        OutlinedTextField(
                            state = rememberTextFieldState(initialText = ""),
                            modifier = Modifier
                                .weight(1f)
                                .visible(false),
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(
                                    alpha = 0f
                                ),
                                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
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
                            onKeyboardAction = { _ ->
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            lineLimits = TextFieldLineLimits.SingleLine
                        )

                        Spacer(
                            modifier = Modifier
                                .height(MaterialTheme.dimens.minButtonHeight)
                                .width(MaterialTheme.dimens.minButtonHeight + MaterialTheme.spacing.small)
                        )
                    }
                }

                if (kind == EditorKind.Pages) {
                    item("languageBar") {
                        Column {
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                pageLanguages.forEach { language ->
                                    FilterChip(
                                        selected = language == selectedLanguage,
                                        onClick = { selectedLanguage = language },
                                        label = { Text(language.uppercase()) }
                                    )
                                }
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        focusManager.clearFocus()
                                        isAddLanguageDialogShown = true
                                    },
                                    label = {
                                        Icon(
                                            imageVector = AddIco,
                                            contentDescription = stringResource(R.string.languageAdd),
                                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                item("spacer") {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.screenHorizontal)
                    )
                }

                itemsIndexed(
                    items = inputList,
                    key = { _, item -> item.id }
                ) { index, item ->
                    ReorderableItem(
                        reorderableState, key = item.id
                    ) { isDragging ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (!isDragging) {
                                        Modifier.animateItem(
                                            fadeInSpec = tween(250),
                                            fadeOutSpec = tween(250),
                                            placementSpec = tween(250)
                                        )
                                    } else Modifier
                                ),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shadowElevation = if (isDragging) MaterialTheme.dimens.shadowElevation else 0.dp
                        ) {
                            when (item) {
                                is EpisodeDraft -> EpisodeRow(
                                    item = item,
                                    hasNumberError = item.number == null || item.number in (inputList - listOf(
                                        item
                                    ).toSet()).filterIsInstance<EpisodeDraft>().map { it.number },
                                    onNumberChange = { n ->
                                        inputList.updateEpisode(item.id) {
                                            it.copy(
                                                number = n
                                            )
                                        }
                                    },
                                    onNumberCommit = { inputList.sortEpisodesByNumber() },
                                    onNameChange = { n ->
                                        inputList.updateEpisode(item.id) {
                                            it.copy(
                                                name = n
                                            )
                                        }
                                    },
                                    onPreviewChange = { p ->
                                        inputList.updateEpisode(item.id) {
                                            it.copy(
                                                image = p
                                            )
                                        }
                                    },
                                    onVoiceChange = { variantId, voice ->
                                        inputList.setVoice(item.id, variantId, voice)
                                        lastVoice = voice
                                    },
                                    onDeleteVariant = { variantId ->
                                        inputList.removeVariant(
                                            item.id,
                                            variantId
                                        )
                                    },
                                    onAddVariant = {
                                        pendingEpisodeId = item.id
                                        pickVideos()
                                    },
                                    onDelete = { inputList.removeAll { it.id == item.id } }
                                )

                                is ChapterDraft -> ChapterRow(
                                    item = item,
                                    hasNumberError = item.number == null || item.number in duplicates,
                                    onNumberChange = { n ->
                                        inputList.updateChapter(item.id) {
                                            it.copy(
                                                number = n
                                            )
                                        }
                                    },
                                    onNumberCommit = { inputList.sortChaptersByNumber() },
                                    onNameChange = { n ->
                                        inputList.updateChapter(item.id) {
                                            it.copy(
                                                name = n
                                            )
                                        }
                                    },
                                    onEditPages = { onEditChapterPages(item) },
                                    onDelete = { inputList.removeAll { it.id == item.id } }
                                )

                                is PageDraft -> {
                                    PageRow(
                                        page = item,
                                        position = index + 1,
                                        language = language,
                                        onImageChange = { image ->
                                            inputList.setPageImage(
                                                item.id,
                                                language,
                                                image
                                            )
                                        },
                                        onDelete = { inputList.removeAll { it.id == item.id } },
                                        dragModifier = Modifier.draggableHandle(
                                            onDragStarted = { focusManager.clearFocus() }
                                        )
                                    )
                                }

                                else -> Unit
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.small)
                    )
                }

                if (inputList.isEmpty()) {
                    item("nothingIsHereText") {
                        Box(modifier = Modifier.fillParentMaxSize()) {
                            Text(
                                text = stringResource(R.string.nothingIsHere),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(
                    start = MaterialTheme.spacing.screenHorizontal + leftInset,
                    top = MaterialTheme.spacing.screenHorizontal + topInset,
                    end = MaterialTheme.spacing.screenHorizontal + rightInset
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CloseButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    focusManager.clearFocus()
                    onClose()
                }
            )

            OutlinedTextField(
                state = searchState,
                modifier = Modifier
                    .weight(1f),
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
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
                lineLimits = TextFieldLineLimits.SingleLine
            )

            var isExtraOptionsExpanded by rememberSaveable { mutableStateOf(false) }
            Box {
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    focusManager.clearFocus()
                    isExtraOptionsExpanded = !isExtraOptionsExpanded
                }) {
                    Icon(
                        imageVector = MoreVertIco,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                    )
                }

                DropDownMenuWithBlur(
                    expanded = { isExtraOptionsExpanded },
                    hazeState = LocalHazeStates.current.hazeStates[LocalLayerIndex.current],
                    onDismissRequest = { isExtraOptionsExpanded = false }
                ) {
                    PopupMenuItem(
                        text = stringResource(R.string.nothingIsHere),
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                        onClick = {}
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.screenHorizontal + bottomInset)
                    .padding(horizontal = MaterialTheme.spacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        onAddItem()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AddIco,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                        )
                        Text(
                            text = stringResource(R.string.add),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                        when (kind) {
                            EditorKind.Episodes -> inputList.sortEpisodesByNumber()
                            EditorKind.Chapters -> inputList.sortChaptersByNumber()
                            EditorKind.Pages -> Unit
                        }
                        resultSenderViewModel.sendResult(
                            requestKey = layer.key,
                            data = PageWithSearchSaveOutput(inputList.toList())
                        )
                        onClose()
                    },
                    enabled = canSave,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.Save),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }

    if (isAddLanguageDialogShown) {
        AddLanguageDialog(
            onDismiss = { isAddLanguageDialogShown = false },
            onConfirm = { code ->
                addedLanguages = (addedLanguages + code).distinct()
                selectedLanguage = code
                isAddLanguageDialogShown = false
            }
        )
    }

    // Результат вложенного редактора страниц возвращается в главу
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(resultSenderViewModel.results, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            resultSenderViewModel.results.collect { (key, data) ->
                val pendingIndex = pendingKeys.indexOfFirst { it.second == key }
                if (pendingIndex == -1) return@collect

                val chapterId = pendingKeys.removeAt(pendingIndex).first
                val output = data as? PageWithSearchSaveOutput
                if (output != null) {
                    inputList.updateChapter(chapterId) {
                        it.copy(pages = output.items.filterIsInstance<PageDraft>())
                    }
                }
            }
        }
    }
}

private enum class EditorKind {
    Episodes, Chapters, Pages;

    fun accepts(item: PageWithSearchItem) = when (this) {
        Episodes -> item is EpisodeDraft
        Chapters -> item is ChapterDraft
        Pages -> item is PageDraft
    }

    companion object {
        fun of(initType: PageWithSearchItem) = when (initType) {
            is EpisodeDraft -> Episodes
            is ChapterDraft -> Chapters
            else -> Pages
        }
    }
}

@Parcelize
data class PageWithSearchInput(
    var items: List<PageWithSearchItem>,
    val initType: PageWithSearchItem  // Этот элемент в списке не отображается
) : Parcelable

/**
 * Всё, кроме id, необязательно: конкретные типы (EpisodeDraft, ChapterDraft, PageDraft)
 * переопределяют только то, что им нужно.
 */
interface PageWithSearchItem : Parcelable {
    val id: Long
    val name: String? get() = null
    val image: ImageData? get() = null
    fun copyWithName(newName: String): PageWithSearchItem = this
    fun copyWithImage(newImage: ImageData?): PageWithSearchItem = this
}

@Deprecated("Используй newDraftId()", ReplaceWith("newDraftId()"))
fun generateNewChapterId(): Long = newDraftId()

object PageWithSearchItemsDefaults {
    val BaseChapter = ChapterDraft.Empty
    val BaseEpisode = EpisodeDraft.Empty
    val BaseChapterPage = PageDraft.Empty
}
