package com.example.garden.repository.objects

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import androidx.room.withTransaction
import com.example.garden.database.AppDatabase
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.dao.ObjectDataDao
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.database.entities.PageType
import com.example.garden.utils.getArtistType
import com.example.garden.utils.toLongId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObjectRepositoryImpl(
    private val db: AppDatabase,
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
                    it.info.injectLocalObjectEntityData(
                        it
                    ) as ObjectData.Carousel
                }
        }
    }

    override fun getUnspecifiedPageObjects(): Flow<PagingData<ObjectData.Card>> {
        return Pager(
            config = PagingConfig(pageSize = 50, enablePlaceholders = false, prefetchDistance = 10),
            pagingSourceFactory = { dao.getUnspecifiedPageObjects() }
        ).flow.map { pagingData ->
            pagingData.map { it.info.injectLocalObjectEntityData(it) }
                .filter { it is ObjectData.Card }.map { it as ObjectData.Card }
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
                            parent = it.parent.info.injectLocalObjectEntityData(
                                it.parent
                            ) as ObjectData.Carousel,
                            childs = it.childs
                                .filter { child -> child.info is ObjectData.Card }
                                .take(30)
                                .map { child ->
                                    child.info.injectLocalObjectEntityData(
                                        child
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
                    it.info.injectLocalObjectEntityData(
                        it
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
                    it.info.injectLocalObjectEntityData(
                        it
                    ) as ObjectData.Card
                }
        }
    }

    override fun searchCards(
        query: String,
        allowedTypes: List<ElementType>
    ): Flow<PagingData<ObjectEntity>> {
        val cleanQuery = query.trim()
        val typeNames = allowedTypes.map { it.name }
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false, prefetchDistance = 6),
            pagingSourceFactory = {
                if (cleanQuery.isEmpty()) {
                    dao.getAllCardsPaging(typeNames)
                } else {
                    dao.searchCardsFts(cleanQuery, typeNames)
                }
            }
        ).flow
    }

    override fun globalSearch(query: String): Flow<List<ObjectEntity>> = dao.globalSearch(query)

    override fun getCardsByParentId(parentId: Long): Flow<List<ObjectEntity>> =
        dao.getCardsByParentId(parentId)

    override suspend fun saveObject(
        data: ObjectData,
        page: PageType,
        parentId: Long?,
        isUserCreated: Boolean
    ): Long = db.withTransaction {
        val elementType = resolveElementType(data)

        suspend fun processAuthor(author: String, card: ObjectData.Card): String {
            val trimmed = author.trim()
            if (trimmed.isEmpty()) return ""

            val existingId = dao.isAuthorExist(trimmed.lowercase())
            return if (existingId != null) {
                dao.getObjectById(existingId)?.name ?: trimmed
            } else {
                val maxPosition = getMaxChildPosition(1L) ?: -1
                val artistType = card.getArtistType()

                if (artistType != null) {
                    dao.upsertObject(
                        entity = ObjectEntity(
                            parentId = 1L,
                            page = PageType.Unspecified,
                            position = maxPosition + 1,
                            source = EntitySourceType.Local,
                            link = LinkData.Self,
                            isUserCreated = true,
                            name = trimmed,
                            elementType = ElementType.ArtistCard,
                            info = ObjectData.Card.Artist(
                                name = trimmed,
                                image = ImageData.Url(""),
                                artistType = artistType
                            )
                        )
                    )
                }
                trimmed
            }
        }

        val newData = if (data is ObjectData.Card) {
            if (data is ObjectData.Card.Music) {
                val updatedAuthors = data.authors
                    .map { processAuthor(it, data) }
                    .filter { it.isNotEmpty() }

                if (updatedAuthors.isNotEmpty()) data.copyWithAuthors(updatedAuthors) else data
            } else {
                if (data.author.isNotBlank()) {
                    val updatedAuthor = processAuthor(data.author, data)
                    data.copyWithAuthors(listOf(updatedAuthor))
                } else {
                    data
                }
            }
        } else {
            data
        }

        val entity = ObjectEntity(
            id = newData.id.toLongId(),
            parentId = parentId,
            page = page,
            position = newData.position,
            elementType = elementType,
            link = newData.link ?: LinkData.None,
            info = newData,
            isUserCreated = isUserCreated,
            name = newData.name,
            source = EntitySourceType.Local
        )

        dao.upsertObject(entity)
    }

    override suspend fun deleteObject(id: Long, parentId: Long?, position: Int, author: String?) =
        dao.deleteObject(id, parentId, position, author)

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
                    return entity.info.injectLocalObjectEntityData(
                        entity = entity
                    )
                }
            }
        }

        return null
    }

    override suspend fun updatePositions(cards: List<ObjectData.Card>) {
        db.withTransaction {
            cards.forEach { card ->
                dao.updatePosition(id = card.id.toLongId(), newPosition = card.position)
            }
        }
    }

    override suspend fun getById(id: Long): ObjectEntity? = dao.getObjectById(id)

    override suspend fun saveCarouselWithChilds(
        carousel: ObjectData.Carousel,
        page: PageType,
        isUserCreated: Boolean
    ): Long {
        val carouselId = saveObject(carousel, page, parentId = null, isUserCreated = isUserCreated)
        return carouselId
    }

    private fun resolveElementType(data: ObjectData): ElementType {
        return when (data) {
            is ObjectData.Carousel -> ElementType.Carousel
            is ObjectData.Card.Anime, is ObjectData.Card.AniLibria -> ElementType.AnimeCard
            is ObjectData.Card.Manga -> ElementType.MangaCard
            is ObjectData.Card.Music -> ElementType.MusicCard
            is ObjectData.Card.Playlist -> ElementType.PlaylistCard
            is ObjectData.Card.Album -> ElementType.AlbumCard
            is ObjectData.Card.Artist -> ElementType.ArtistCard
        }
    }

    override suspend fun getMaxChildPosition(parentId: Long?): Int? =
        dao.getMaxChildPosition(parentId)
}