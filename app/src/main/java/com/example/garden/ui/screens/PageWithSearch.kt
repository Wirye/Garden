package com.example.garden.ui.screens

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.garden.Layer
import com.example.garden.LocalCustomColors
import com.example.garden.R
import com.example.garden.ResultKeys
import com.example.garden.database.ImageData
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.ui.components.AsyncImageWithAddPlaceholder
import com.example.garden.ui.components.SmartFilePicker
import com.example.garden.ui.components.icons.AddIco
import com.example.garden.ui.components.icons.CloseIco
import com.example.garden.ui.components.icons.DeleteIco
import com.example.garden.ui.components.icons.DragHandleIco
import com.example.garden.ui.components.icons.EditIco
import com.example.garden.ui.components.icons.MoreVertIco
import com.example.garden.ui.components.icons.SearchIco
import com.example.garden.ui.components.rememberFilePicker
import com.example.garden.ui.theme.dimens
import com.example.garden.ui.theme.spacing
import com.example.garden.ui.utils.blockGestures
import com.example.garden.ui.utils.clearFocus
import com.example.garden.ui.utils.dataForModel
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

@Composable
fun PageWithSearch(
    layer: Layer.PageWithSearch,
    resultSenderViewModel: ResultSenderViewModel,
    layersViewModel: LayersViewModel,
    onClose: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val input = remember(layer.startsInfo) { layer.startsInfo }

    val focusManager = LocalFocusManager.current

    val forSaveInLayer: (List<PageWithSearchItem>) -> Unit = remember(layer) {
        {
            layer.startsInfo.items = it
        }
    }

    val currentPendingKeys = remember { mutableListOf<Pair<Long, String>>() }

    val onEditChilds: ((PageWithSearchItem) -> Unit) = remember(resultSenderViewModel) {
        {
            val childs = it.childs
            if (childs != null) {
                val key = ResultKeys.getPageWithEditSearchKey()
                currentPendingKeys.add(Pair(it.id, key))
                layersViewModel.openLayer(
                    Layer.PageWithSearch(
                        key = key,
                        startsInfo = PageWithSearchInput(
                            items = childs,
                            initType = when(it.javaClass) {
                                ChapterInfo::class.java -> PageWithSearchItemsDefaults.BaseChapterPage
                                else -> PageWithSearchItemsDefaults.BaseChapterPage
                            }
                        )
                    )
                )
            }
        }
    }

    val inputList = remember(input.items) {
        input.items
            .filter { it.javaClass == input.initType.javaClass }
            .toMutableStateList()
    }

    LaunchedEffect(inputList) {
        forSaveInLayer(inputList)
    }

    val type = remember(input.initType) {
        when (input.initType) {
            is EpisodeInfo -> arrayOf("video/*")
            is ChapterPageInfo -> arrayOf("image/*")
            else -> arrayOf("image/*")
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val addItemFilePicker = rememberFilePicker(
        mimeTypes = type
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                when (input.initType.javaClass) {
                    EpisodeInfo::class.java -> inputList.add(
                        EpisodeInfo(
                            id = generateNewChapterId(),
                            name = "",
                            link = LinkData(type = LinkType.CONTENT, targetId = null, contentPath = uri.toString()),
                            length = context.getMediaDuration(uri.toString()),
                            image = null
                        )
                    )
                    ChapterPageInfo::class.java -> inputList.add(
                        ChapterPageInfo(
                            id = generateNewChapterId(),
                            image = ImageData.Device(uri.toString()),
                            name = null
                        )
                    )
                    ChapterInfo::class.java -> inputList.add(
                        ChapterInfo(
                            id = generateNewChapterId(),
                            name = "",
                            link = LinkData(type = LinkType.SELF, targetId = null, contentPath = null),
                            childs = mutableListOf()
                        )
                    )
                    else -> {}
                }
            }
        }
    }

    val onAddItem = {
        if (input.initType !is ChapterInfo) {
            addItemFilePicker()
        } else {
            inputList.add(
                ChapterInfo(
                    id = generateNewChapterId(),
                    name = "",
                    link = LinkData(type = LinkType.SELF, targetId = null, contentPath = null),
                    childs = mutableListOf(),
                    length = null,
                    image = null
                )
            )
        }
    }

    val firstVisibleItem = { layer.firstElementPosition }
    val searchState = rememberTextFieldState(initialText = "")
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = firstVisibleItem())

    LaunchedEffect(inputList) {
        snapshotFlow { inputList.toList() }
            .collect { updatedList ->
                forSaveInLayer(updatedList)
            }
    }

    val reorderableState = rememberReorderableLazyListState(
        listState,
        PaddingValues(0.dp)
    ) { from, to ->
        inputList.add(to.index, inputList.removeAt(from.index))
    }

    val density = LocalDensity.current
    val topInset = with(density) { WindowInsets.safeDrawing.getTop(density).toDp() }
    val bottomInset = with(density) { WindowInsets.safeDrawing.getBottom(density).toDp() }
    val leftInset = with(density) { WindowInsets.safeDrawing.getLeft(density, LocalLayoutDirection.current).toDp() }
    val rightInset = with(density) { WindowInsets.safeDrawing.getRight(density, LocalLayoutDirection.current).toDp() }

    val searchText = searchState.text.toString()
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            val names = inputList.mapNotNull { it.name }
            val searchResults = searchInList(names, searchText, minScore = 50)
            if (searchResults.isNotEmpty()) {
                val matchedName = searchResults.first()
                val targetIndex = inputList.indexOfFirst { it.name == matchedName }
                if (targetIndex != -1) {
                    listState.animateScrollToItem(
                        index = targetIndex,
                        scrollOffset = -20
                    )
                }
            }
        }
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                top = MaterialTheme.spacing.screenHorizontal + topInset,
                start = MaterialTheme.spacing.screenHorizontal + leftInset,
                end = MaterialTheme.spacing.screenHorizontal + rightInset
            )
            .blockGestures()
            .clearFocus(focusManager),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.screenHorizontal)
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
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = LocalCustomColors.current.closeButton,
                    contentColor = LocalCustomColors.current.onCloseButton
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = CloseIco,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                )
            }

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

            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                focusManager.clearFocus()
            }) {
                Icon(
                    imageVector = MoreVertIco,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing.extraLarge + MaterialTheme.spacing.medium + bottomInset)
            ) {
                itemsIndexed(
                    items = inputList,
                    key = { _, item -> item.id }
                ) { index, item ->
                    val useHolderIndex = item is ChapterInfo

                    ReorderableItem(reorderableState, key = item.id) { isDragging ->
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
                            PageWithSearchItem(
                                item = item,
                                holderIndex = index + 1,
                                useHolderIndexInsteadOfImage = useHolderIndex,
                                onNameChange = { newName ->
                                    inputList[index] = item.copyWithName(newName)
                                },
                                onImageChange = { newImage ->
                                    inputList[index] = item.copyWithImage(newImage)
                                },
                                onDelete = {
                                    inputList.removeAt(index)
                                },
                                onEdit = {
                                    onEditChilds(item)
                                },
                                dragModifier = Modifier.draggableHandle(
                                    onDragStarted = { focusManager.clearFocus() }
                                )
                            )
                        }
                    }
                }
                if (inputList.isEmpty()) {
                    item("nothingIsHereText") {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                        ) {
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

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onAddItem()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = MaterialTheme.spacing.medium),
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
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    resultSenderViewModel.sendResult(requestKey = layer.key, data = PageWithSearchSaveOutput(inputList))
                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.screenHorizontal + bottomInset),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.Save),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)
                )
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(resultSenderViewModel.results, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            resultSenderViewModel.results.collect { (key, data) ->
                if (key in currentPendingKeys.map { it.second }) {
                    val newData = data as? PageWithSearchSaveOutput

                    val index = currentPendingKeys.indexOfFirst { it.second == key }

                    if (newData != null && index != -1) {
                        val keyItem = currentPendingKeys[index]
                        val inputListItemIndex = inputList.indexOfFirst { it.id == keyItem.first }
                        if (inputListItemIndex != -1) {
                            inputList[inputListItemIndex] = inputList[inputListItemIndex].copyWithChilds(newData.items.toMutableList())
                            forSaveInLayer(inputList)
                        }
                    }

                    if (index != -1) {
                        currentPendingKeys.removeAt(index)
                    }
                }
            }
        }
    }
}

@Composable
private fun PageWithSearchItem(
    @SuppressLint("ModifierParameter") dragModifier: Modifier = Modifier,
    item: PageWithSearchItem,
    holderIndex: Int,
    useHolderIndexInsteadOfImage: Boolean,
    onNameChange: (String) -> Unit,
    onImageChange: (ImageData) -> Unit?,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    val nameState = rememberTextFieldState(initialText = item.name ?: "")

    LaunchedEffect(nameState.text) {
        if (nameState.text.toString() != item.name && item.name != null) {
            onNameChange(nameState.text.toString())
        }
    }

    val image = remember(item.image) { mutableStateOf(item.image) }

    val isImageChangeDialogExpanded = remember { mutableStateOf(false) }

    val openImagePicker = rememberFilePicker(
        mimeTypes = arrayOf("image/*")
    ) { uri ->
        if (uri != null) {
            val newImage = ImageData.Device(uri.toString())
            image.value = newImage
            onImageChange(newImage)
        }
    }

    SmartFilePicker(
        expanded = isImageChangeDialogExpanded.value,
        onDismiss = { isImageChangeDialogExpanded.value = false },
        onFileDelete = {
            isImageChangeDialogExpanded.value = false
        },
        onFileChange = {
            isImageChangeDialogExpanded.value = false
            openImagePicker()
        },
        hazeState = LocalHazeLayers.current.mainScreen
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
            modifier = Modifier.weight(1f, fill = true)
        ) {
            if (useHolderIndexInsteadOfImage) {
                Text(
                    text = "#$holderIndex",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                AsyncImageWithAddPlaceholder(
                    model = image.value?.dataForModel(),
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            isImageChangeDialogExpanded.value = true
                        }),
                    shape = MaterialTheme.shapes.small
                )
            }

            if (item.name != null) {
                OutlinedTextField(
                    state = nameState,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.title),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
        ) {
            if (item.childs != null) {
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onEdit()
                }) {
                    Icon(
                        imageVector = EditIco,
                        contentDescription = "Редактировать",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                    )
                }
            }

            Icon(
                imageVector = DragHandleIco,
                contentDescription = "Переместить",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = dragModifier
                    .padding(MaterialTheme.spacing.small)
                    .size(MaterialTheme.dimens.iconMedium)
            )

            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onDelete()
            }) {
                Icon(
                    imageVector = DeleteIco,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                )
            }
        }
    }
}


@Parcelize
data class PageWithSearchInput(
    var items: List<PageWithSearchItem>,
    val initType: PageWithSearchItem  // This element will not be displayed in the list
) : Parcelable

interface PageWithSearchItem: Parcelable {
    val id: Long
    val name: String?
    val link: LinkData?
    val length: Long?
    val image: ImageData?
    val childs: MutableList<PageWithSearchItem>?
    fun copyWithName(newName: String): PageWithSearchItem
    fun copyWithImage(newImage: ImageData?): PageWithSearchItem
    fun copyWithChilds(newChilds: MutableList<PageWithSearchItem>?): PageWithSearchItem
}

private var episodeId = 0L
private fun generateNewChapterId(): Long {
    val res = episodeId
    episodeId += 1L
    return res
}

@Parcelize
data class EpisodeInfo(
    override val id: Long,
    override val image: ImageData?,
    override val name: String,
    override val link: LinkData,
    override val length: Long,
    override val childs: MutableList<PageWithSearchItem>? = null,
) : PageWithSearchItem {
    override fun copyWithName(newName: String) = copy(name = newName)
    override fun copyWithImage(newImage: ImageData?) = copy(image = newImage)
    override fun copyWithChilds(newChilds: MutableList<PageWithSearchItem>?) = copy()
}

@Parcelize
data class ChapterInfo(
    override val id: Long,
    override val image: ImageData? = null,
    override val name: String,
    override val link: LinkData,
    override val length: Long? = null,
    override val childs: MutableList<PageWithSearchItem>
) : PageWithSearchItem {
    override fun copyWithName(newName: String) = copy(name = newName)
    override fun copyWithImage(newImage: ImageData?) = copy()
    override fun copyWithChilds(newChilds: MutableList<PageWithSearchItem>?) = copy(childs = newChilds ?: mutableListOf())
}

@Parcelize
data class ChapterPageInfo(
    override val id: Long,
    override val image: ImageData,
    override val name: String? = null,
    override val link: LinkData? = null,
    override val length: Long? = null,
    override val childs: MutableList<PageWithSearchItem>? = null,
) : PageWithSearchItem {
    override fun copyWithName(newName: String) = copy(name = newName)
    override fun copyWithImage(newImage: ImageData?) = copy(
        image = newImage ?: ImageData.Device("")
    )

    override fun copyWithChilds(newChilds: MutableList<PageWithSearchItem>?) = copy()
}

object PageWithSearchItemsDefaults {
   val BaseChapter = ChapterInfo(
        id = 0L,
        name = "",
        link = LinkData(type = LinkType.SELF, targetId = null, contentPath = null),
        childs = mutableListOf()
   )

    val BaseEpisode = EpisodeInfo(
        id = 0L,
        name = "",
        link = LinkData(type = LinkType.CONTENT, targetId = null, contentPath = null),
        length = 0L,
        image = null
    )

    val BaseChapterPage = ChapterPageInfo(
        id = 0L,
        name = null,
        image = ImageData.Device(""),
        link = null,
        length = null
    )
}
