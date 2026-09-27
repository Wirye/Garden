package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.entities.CollectionType
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.LayoutType
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.database.entities.PageType
import com.example.garden.repository.objects.ObjectRepository
import com.example.garden.repository.uiRepository.UiRepository
import com.example.garden.utils.toLongId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

sealed interface CarouselState {
    data object Success : CarouselState
    data object Loading : CarouselState
    data class Error(val message: String) : CarouselState
}

class MainViewModel(
    private val repository: ObjectRepository,
    private val uiRepository: UiRepository
) : ViewModel() {
    private val _carouselStates = MutableStateFlow<Map<String, CarouselState>>(emptyMap())
    val carouselStates: StateFlow<Map<String, CarouselState>> = _carouselStates.asStateFlow()

    private val pagingFlowsCache =
        ConcurrentHashMap<PageType, Flow<PagingData<ObjectWithChilds2>>>()

    fun getPagePagingObjects(page: PageType): Flow<PagingData<ObjectWithChilds2>> {
        return pagingFlowsCache.getOrPut(page) {
            uiRepository.getPagePagingObjects(page)
                .cachedIn(viewModelScope)
        }
    }

    private val unspecifiedPagingFlowCache =
        ConcurrentHashMap<Int, Flow<PagingData<ObjectData.Card>>>()

    fun getUnspecifiedPageObjects(): Flow<PagingData<ObjectData.Card>> {
        return unspecifiedPagingFlowCache.getOrPut(0) {
            repository.getUnspecifiedPageObjects()
                .cachedIn(viewModelScope)
        }
    }

    fun insertSavedCarousel() {
        viewModelScope.launch {
            repository.saveObject(
                ObjectData.Carousel(
                    id = "${EntitySourceType.Local.name}_1",
                    childsShowName = true,
                    childsShowAuthor = true,
                    maxLines = null,
                    objectsInOneLine = 1,
                    layoutType = LayoutType.CAROUSEL_FROM_FLAT_GRID,
                    carouselCollectionType = CollectionType.None,
                ),
                page = PageType.Unspecified
            )
        }
    }

    suspend fun updatePositions(cards: List<ObjectData.Card>) = repository.updatePositions(cards)

    fun getCardsByParentId(parentId: Long): Flow<List<ObjectEntity>> =
        repository.getCardsByParentId(parentId)

    suspend fun getRootObjectById(id: Long): ObjectData? = repository.getRootObjectById(id)

    suspend fun saveObject(data: ObjectData, page: PageType, parentId: Long?): Long {
        val objPosition = repository.getById(data.id.toLongId())?.position

        val maxPosition = repository.getMaxChildPosition(parentId)

        val newData = if (data.position != -1) data.copyWithPosition(
            position = objPosition ?: data.position
        ) else data.copyWithPosition(
            position = maxPosition?.plus(1) ?: 0
        )

        return repository.saveObject(newData, page, parentId)
    }

    suspend fun insertObjectWithLinkInsert(parentId: Long, targetId: Long) {
        val obj = repository.getById(targetId) ?: return
        when (obj.info) {
            is ObjectData.Card.Anime -> {
                saveObject(
                    data = ObjectData.Card.Anime(
                        id = "0L",
                        position = -1,
                        name = obj.info.name,
                        image = obj.info.image,
                        link = LinkData.Insert(targetId)
                    ),
                    page = PageType.Home,
                    parentId = parentId
                )
            }
            is ObjectData.Card.Music -> {
                saveObject(
                    data = ObjectData.Card.Music(
                        id = "0L",
                        position = -1,
                        name = obj.info.name,
                        image = obj.info.image,
                        link = LinkData.Insert(targetId)
                    ),
                    page = PageType.Home,
                    parentId = parentId
                )
            }
            is ObjectData.Card.Manga -> {
                saveObject(
                    data = ObjectData.Card.Manga(
                        id = "0L",
                        position = -1,
                        name = obj.info.name,
                        image = obj.info.image,
                        link = LinkData.Insert(targetId)
                    ),
                    page = PageType.Home,
                    parentId = parentId
                )
            }
            is ObjectData.Card.Playlist -> {
                saveObject(
                    data = ObjectData.Card.Playlist(
                        id = "0L",
                        position = -1,
                        name = obj.info.name,
                        image = obj.info.image,
                        link = LinkData.Insert(targetId),
                        playListType = obj.info.playListType
                    ),
                    page = PageType.Home,
                    parentId = parentId
                )
            }
            is ObjectData.Card.AniLibria -> {
                saveObject(
                    data = ObjectData.Card.AniLibria(
                        id = "0L",
                        position = -1,
                        name = obj.info.name,
                        image = obj.info.image,
                        link = LinkData.Insert(targetId),
                        anilibriaCardId = obj.info.anilibriaCardId,
                        data = obj.info.data
                    ),
                    page = PageType.Home,
                    parentId = parentId
                )
            }
            is ObjectData.Card.Artist -> {
                saveObject(
                    data = ObjectData.Card.Artist(
                        id = "0L",
                        position = -1,
                        name = obj.info.name,
                        image = obj.info.image,
                        link = LinkData.Insert(targetId),
                        artistType = obj.info.artistType
                    ),
                    page = PageType.Home,
                    parentId = parentId
                )
            }
            is ObjectData.Card.Album -> {
                saveObject(
                    data = ObjectData.Card.Album(
                        id = "0L",
                        position = -1,
                        name = obj.info.name,
                        image = obj.info.image,
                        link = LinkData.Insert(targetId)
                    ),
                    page = PageType.Home,
                    parentId = parentId
                )
            }

            is ObjectData.Carousel -> {}
        }
    }

    suspend fun deleteObjectById(id: Long) {
        val obj = repository.getById(id) ?: return
        repository.deleteObject(id = obj.id, parentId = obj.parentId, position = obj.position, author = if (obj.info is ObjectData.Card.Artist) obj.name else null)
    }
}