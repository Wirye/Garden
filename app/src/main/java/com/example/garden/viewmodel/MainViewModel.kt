package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.garden.database.CardSize
import com.example.garden.database.ImageData
import com.example.garden.database.LayoutType
import com.example.garden.database.LinkData
import com.example.garden.database.ObjectData
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.PageType
import com.example.garden.repository.ObjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class MainViewModel(
    private val repository: ObjectRepository
) : ViewModel() {
    private val pagingFlowsCache =
        ConcurrentHashMap<PageType, Flow<PagingData<ObjectWithChilds2>>>()

    fun getPagePagingObjects(page: PageType): Flow<PagingData<ObjectWithChilds2>> {
        return pagingFlowsCache.getOrPut(page) {
            repository.getPagePagingObjects(page)
                .cachedIn(viewModelScope)
        }
    }

    fun insert() {
        viewModelScope.launch {
            repository.saveObject(
                ObjectData.Carousel(
                    id = 1L,
                    position = 0,
                    name = "Ничегооооооооооооооооооооооооо",
                    childsShowName = true,
                    childsShowAuthor = true,
                    maxLines = 4,
                    layoutType = LayoutType.CAROUSEL_FROM_FLAT_GRID,
                    dovodchik = true,
                    showDovodchikDots = true
                ),
                page = PageType.Home
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 2L,
                    position = 0,
                    name = "Ничего",
                    image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 1L
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 3L,
                    position = 1,
                    name = "Ничего",
                    image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 1L
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 4L,
                    position = 2,
                    name = "Ничего",
                    image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 1L
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 5L,
                    position = 3,
                    name = "Ничего",
                    image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 1L
            )

            repository.saveObject(
                ObjectData.Carousel(
                    id = 6L,
                    position = 1,
                    name = "Ничего",
                    childsShowName = true,
                    childsShowAuthor = true,
                    childsSize = CardSize.SMALL,
                    layoutType = LayoutType.CAROUSEL_FROM_GRID,
                    objectsInOneLine = 2,
                    maxLines = 1,
                    dovodchik = true,
                    showDovodchikDots = true
                ),
                page = PageType.Home
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 7L,
                    position = 0,
                    name = "Ничего",
                    image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 6L
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 8L,
                    position = 1,
                    name = "Ничего",
                    image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 6L
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 9L,
                    position = 2,
                    name = "Ничего",
                    image = ImageData.Url("https://www.aniliberty.top/storage/releases/posters/10290/tc8bOapcxYF5xJ3M2UNjiklndO8iYioQ.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 6L
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 10L,
                    position = 3,
                    name = "Ничего",
                    image = ImageData.Url("https://www.aniliberty.top/storage/releases/posters/10290/tc8bOapcxYF5xJ3M2UNjiklndO8iYioQ.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 6L
            )

            repository.saveObject(
                ObjectData.Card.Anime(
                    id = 11L,
                    position = 4,
                    name = "Ничего",
                    image = ImageData.Url("https://www.aniliberty.top/storage/releases/posters/10290/tc8bOapcxYF5xJ3M2UNjiklndO8iYioQ.webp"),
                    link = LinkData.Self
                ),
                page = PageType.Home,
                parentId = 6L
            )
        }
    }

    suspend fun getRootObjectById(id: Long): ObjectData? = repository.getRootObjectById(id)

    suspend fun saveObject(data: ObjectData, page: PageType, parentId: Long?): Long {
        val objPosition = repository.getById(data.id)?.position

        val maxPosition = repository.getMaxChildPosition(parentId)

        val newData = if (data.position != -1) data.copyWithIdAndPositionAndLink(
            id = data.id,
            position = objPosition ?: data.position,
            link = data.link ?: LinkData.None
        ) else data.copyWithIdAndPositionAndLink(
            id = data.id,
            position = maxPosition?.plus(1) ?: 0,
            link = data.link ?: LinkData.None
        )

        return repository.saveObject(newData, page, parentId)
    }

    suspend fun deleteObjectById(id: Long) {
        val obj = repository.getById(id) ?: return
        repository.deleteObject(id = obj.id, parentId = obj.parentId, position = obj.position)
    }
}