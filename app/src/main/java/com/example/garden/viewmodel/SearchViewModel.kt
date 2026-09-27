package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.garden.database.entities.ArtistType
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.repository.aniLibriaSearch.AniLibriaSearchRepository
import com.example.garden.repository.artists.ArtistsRepository
import com.example.garden.repository.objects.ObjectRepository
import com.example.garden.utils.search.buildFuzzyFtsQuery
import com.example.garden.utils.toObjectEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

sealed interface ListSearchState {
    data object Idle : ListSearchState
    data object Loading : ListSearchState
    data class Success(val items: List<ObjectEntity>) : ListSearchState
    data object Empty : ListSearchState
}

private data class SearchCardsState(
    val query: String = "",
    val isSearching: Boolean = false,
    val allowedTypes: List<ElementType> = emptyList()
)

private data class SearchArtistsState(
    val query: String = "",
    val isSearching: Boolean = false,
    val allowedTypes: List<ArtistType> = emptyList()
)

class SearchViewModel(
    private val objectRepository: ObjectRepository,
    private val aniLibriaSearchRepository: AniLibriaSearchRepository,
    private val artistsRepository: ArtistsRepository
) : ViewModel() {

    private val searchCardsQuery = MutableStateFlow(SearchCardsState("", false, emptyList()))

    fun searchCards(query: String, allowedTypes: List<ElementType>) {
        searchCardsQuery.value = SearchCardsState(query, true, allowedTypes)
    }

    fun clearCardsSearch() {
        searchCardsQuery.value = SearchCardsState("", false, emptyList())
    }

    private val searchArtistsQuery = MutableStateFlow(SearchArtistsState("", false, emptyList()))

    fun searchArtists(query: String, allowedTypes: List<ArtistType>) {
        searchArtistsQuery.value = SearchArtistsState(query, true, allowedTypes)
    }

    fun clearArtistsSearch() {
        searchArtistsQuery.value = SearchArtistsState("", false, emptyList())
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchArtistsFlow: Flow<List<ObjectData.Card.Artist>> = searchArtistsQuery
        .debounce { if (it.isSearching) 300.milliseconds else 0.milliseconds }
        .distinctUntilChanged()
        .flatMapLatest {
            if (it.isSearching) {
                artistsRepository.searchArtists(buildFuzzyFtsQuery(it.query.replace(" ", "")), it.allowedTypes)
            } else {
                flowOf(emptyList())
            }
        }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchCardsFlow: Flow<PagingData<ObjectEntity>> = searchCardsQuery
        .debounce { if (it.isSearching) 300.milliseconds else 0.milliseconds }
        .distinctUntilChanged()
        .flatMapLatest {
            if (it.isSearching) {
                objectRepository.searchCards(buildFuzzyFtsQuery(it.query.replace(" ", "")), it.allowedTypes)
            } else {
                flowOf(PagingData.empty())
            }
        }
        .cachedIn(viewModelScope)

    private val globalSearchQuery = MutableStateFlow(Pair("", false))

    fun globalSearch(query: String) {
        globalSearchQuery.value = Pair(query, true)
    }

    fun clearGlobalSearch() {
        globalSearchQuery.value = Pair("", false)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val localSearchFlow: Flow<ListSearchState> = globalSearchQuery
        .flatMapLatest { (query, isEnabled) ->
            val cleanQuery = query.trim()
            if (isEnabled && cleanQuery.length >= 3) {
                flow<ListSearchState> {
                    emit(ListSearchState.Loading)

                    delay(300.milliseconds)

                    objectRepository.globalSearch(query = buildFuzzyFtsQuery(cleanQuery.replace(" ", "")))
                        .collect { list ->
                            if (list.isNotEmpty()) {
                                emit(ListSearchState.Success(list))
                            } else {
                                emit(ListSearchState.Empty)
                            }
                        }
                }
            } else {
                flowOf(ListSearchState.Idle)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val aniLibriaSearchFlow: Flow<ListSearchState> = globalSearchQuery
        .flatMapLatest { (query, isEnabled) ->
            val cleanQuery = query.trim()
            if (isEnabled && cleanQuery.length >= 3) {
                flow<ListSearchState> {
                    emit(ListSearchState.Loading)

                    delay(1500.milliseconds)

                    try {
                        val response = withTimeoutOrNull(10.seconds) {
                            aniLibriaSearchRepository.search(cleanQuery, 15)
                        }

                        if (response != null) {
                            response.onSuccess { releases ->
                                val entities = releases.mapIndexed { index, title ->
                                    title.toObjectEntity(position = index, sourceType = EntitySourceType.Web)
                                }
                                if (entities.isNotEmpty()) {
                                    emit(ListSearchState.Success(entities))
                                } else {
                                    emit(ListSearchState.Empty)
                                }
                            }.onFailure {
                                emit(ListSearchState.Empty)
                            }
                        } else {
                            emit(ListSearchState.Empty)
                        }
                    } catch (_: Exception) {
                        emit(ListSearchState.Empty)
                    }
                }
            } else {
                flowOf(ListSearchState.Idle)
            }
        }
}