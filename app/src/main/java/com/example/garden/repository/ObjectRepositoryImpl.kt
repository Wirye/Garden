package com.example.garden.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.example.garden.database.ElementType
import com.example.garden.database.LinkData
import com.example.garden.database.ObjectData
import com.example.garden.database.ObjectEntity
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.PageType
import com.example.garden.database.dao.ObjectDataDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObjectRepositoryImpl(
    private val dao: ObjectDataDao
) : ObjectRepository {

    override fun getPagePaging(page: PageType): Flow<PagingData<ObjectData.Carousel>> {
        return Pager(
            config = PagingConfig(pageSize = 16, enablePlaceholders = false, prefetchDistance = 5),
            pagingSourceFactory = { dao.getPagingObjectsByPage(page) }
        ).flow.map { pagingData ->
            pagingData
                .filter { it.info is ObjectData.Carousel }
                .map {
                    it.info.copyWithIdAndPositionAndLink(
                        it.id,
                        it.position,
                        it.link
                    ) as ObjectData.Carousel
                }
        }
    }

    override fun getPagePagingObjects(page: PageType): Flow<PagingData<ObjectWithChilds2>> {
        return Pager(
            config = PagingConfig(
                pageSize = 16,
                prefetchDistance = 3,
                maxSize = 24,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { dao.getRootObjectsWithChildren(page) }
        ).flow
            .map { pagingData ->
                pagingData
                    .filter { it.parent.info is ObjectData.Carousel }
                    .map {
                        ObjectWithChilds2(
                            parent = it.parent.info.copyWithIdAndPositionAndLink(
                                it.parent.id,
                                it.parent.position,
                                it.parent.link
                            ) as ObjectData.Carousel,
                            childs = it.childs
                                .filter { child -> child.info is ObjectData.Card }
                                .take(30)
                                .map { child ->
                                    child.info.copyWithIdAndPositionAndLink(
                                        child.id,
                                        child.position,
                                        child.link
                                    ) as ObjectData.Card
                                }
                        )
                    }
            }
    }

    override fun getCarouselChildrenPaging(parentId: Long): Flow<PagingData<ObjectData.Card>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false, prefetchDistance = 3),
            pagingSourceFactory = { dao.getChildrenPagingByParentId(parentId) }
        ).flow.map { pagingData ->
            pagingData
                .filter { it.info is ObjectData.Card }
                .map {
                    it.info.copyWithIdAndPositionAndLink(
                        it.id,
                        it.position,
                        it.link
                    ) as ObjectData.Card
                }
        }
    }

    override fun getCardsPaging(elementType: ElementType): Flow<PagingData<ObjectData.Card>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, prefetchDistance = 6),
            pagingSourceFactory = { dao.getPagingObjectsByElementType(elementType) }
        ).flow.map { pagingData ->
            pagingData
                .filter { it.info is ObjectData.Card }
                .map {
                    it.info.copyWithIdAndPositionAndLink(
                        it.id,
                        it.position,
                        it.link
                    ) as ObjectData.Card
                }
        }
    }

    override suspend fun saveObject(data: ObjectData, page: PageType, parentId: Long?): Long {
        val elementType = resolveElementType(data)
        val entity = ObjectEntity(
            id = data.id,
            parentId = parentId,
            page = page,
            position = data.position,
            elementType = elementType,
            link = data.link ?: LinkData.None,
            info = data
        )
        return dao.upsertObject(entity)
    }

    override suspend fun deleteObject(id: Long, parentId: Long?, position: Int) =
        dao.deleteObject(id, parentId, position)

    override suspend fun getRootObjectById(id: Long): ObjectData? {
        var currentId: Long? = id
        val visitedIds = mutableSetOf<Long>()

        while (currentId != null) {
            if (!visitedIds.add(currentId)) break

            val entity = dao.getObjectById(currentId) ?: break

            when (val link = entity.link) {
                is LinkData.Insert -> {
                    currentId = link.targetId
                }

                else -> {
                    return entity.info.copyWithIdAndPositionAndLink(
                        id = entity.id,
                        position = entity.position,
                        link = entity.link
                    )
                }
            }
        }

        return null
    }

    override suspend fun getById(id: Long): ObjectEntity? = dao.getObjectById(id)

    override suspend fun saveCarouselWithChilds(
        carousel: ObjectData.Carousel,
        page: PageType
    ): Long {
        val carouselId = saveObject(carousel, page, parentId = null)
        return carouselId
    }

    private fun resolveElementType(domain: ObjectData): ElementType {
        return when (domain) {
            is ObjectData.Carousel -> ElementType.Carousel
            is ObjectData.Card.Anime -> ElementType.AnimeCard
            is ObjectData.Card.Manga -> ElementType.MangaCard
            is ObjectData.Card.Music -> ElementType.MusicCard
            is ObjectData.Card.Playlist -> ElementType.PlaylistCard
        }
    }

    override suspend fun getMaxChildPosition(parentId: Long?): Int? =
        dao.getMaxChildPosition(parentId)
}