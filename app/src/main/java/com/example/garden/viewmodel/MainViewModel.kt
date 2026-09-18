package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.garden.database.ElementType
import com.example.garden.database.ObjectData
import com.example.garden.database.ObjectEntity
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.PageType
import com.example.garden.repository.ObjectRepository
import com.example.garden.utils.search.buildFuzzyFtsQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

sealed interface CarouselState {
    data object Success : CarouselState
    data object Loading : CarouselState
    data class Error(val message: String) : CarouselState
}

private data class SearchCardsState(
    val query: String = "",
    val isSearching: Boolean = false,
    val allowedTypes: List<ElementType> = emptyList()
)

class MainViewModel(
    private val repository: ObjectRepository
) : ViewModel() {
    private val _carouselStates = MutableStateFlow<Map<Long, CarouselState>>(emptyMap())
    val carouselStates: StateFlow<Map<Long, CarouselState>> = _carouselStates.asStateFlow()

    private val pagingFlowsCache =
        ConcurrentHashMap<PageType, Flow<PagingData<ObjectWithChilds2>>>()

    fun getPagePagingObjects(page: PageType): Flow<PagingData<ObjectWithChilds2>> {
        return pagingFlowsCache.getOrPut(page) {
            repository.getPagePagingObjects(page)
                .cachedIn(viewModelScope)
        }
    }

    private val searchCardsQuery = MutableStateFlow(SearchCardsState("", false, emptyList()))

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun searchCards(
        query: String,
        allowedTypes: List<ElementType>
    ) {
        searchCardsQuery.value = SearchCardsState(query, true, allowedTypes)
    }

    fun clearCardsSearch() {
        searchCardsQuery.value = SearchCardsState("", false, listOf())
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchCardsFlow: Flow<PagingData<ObjectEntity>> = searchCardsQuery
        .debounce{
            if (it.isSearching) 300.milliseconds else 0.milliseconds
        }
        .distinctUntilChanged()
        .flatMapLatest {
            if (it.isSearching) {
                repository.searchCards(buildFuzzyFtsQuery(it.query.replace(" ", "")), it.allowedTypes)
            } else {
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)

    suspend fun updatePositions(cards: List<ObjectData.Card>) = repository.updatePositions(cards)

    fun getCardsByParentId(parentId: Long): Flow<List<ObjectEntity>> =
        repository.getCardsByParentId(parentId)

    suspend fun getRootObjectById(id: Long): ObjectData? = repository.getRootObjectById(id)

    suspend fun saveObject(data: ObjectData, page: PageType, parentId: Long?): Long {
        val objPosition = repository.getById(data.id)?.position

        val maxPosition = repository.getMaxChildPosition(parentId)

        val newData = if (data.position != -1) data.copyWithPosition(
            position = objPosition ?: data.position
        ) else data.copyWithPosition(
            position = maxPosition?.plus(1) ?: 0
        )

        return repository.saveObject(newData, page, parentId)
    }

    suspend fun deleteObjectById(id: Long) {
        val obj = repository.getById(id) ?: return
        repository.deleteObject(id = obj.id, parentId = obj.parentId, position = obj.position)
    }
}