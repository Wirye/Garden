package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garden.database.CardSize
import com.example.garden.database.CarouselType
import com.example.garden.database.ElementType
import com.example.garden.database.Genre
import com.example.garden.database.ImageData
import com.example.garden.database.LayoutType
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.database.ObjectData
import com.example.garden.database.ObjectData2
import com.example.garden.database.ObjectDataDao
import com.example.garden.database.PageType
import com.example.garden.database.SizeType
import com.example.garden.database.groups.GroupsData
import com.example.garden.database.groups.GroupsDataDao
import com.example.garden.ui.screens.ChapterInfo
import com.example.garden.ui.screens.EpisodeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val dao: ObjectDataDao,
    private val groupDao: GroupsDataDao
) : ViewModel() {
    val uiDataFlow: StateFlow<List<ObjectData2>> = dao.getAll()
        .map { allItems ->
            val roots = allItems.filter { it.parentId == null }.sortedBy { it.position }
            roots.map { build(it, allItems) }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun build(current: ObjectData, allItems: List<ObjectData>): ObjectData2 {
        val dataSource =
            if (current.link?.type == LinkType.INSERT && current.link?.targetId != null) {
                allItems.find { it.id == current.link?.targetId } ?: current
            } else {
                current
            }
        val childs = allItems.filter {
            it.parentId == if (dataSource != current) {
                dataSource.id
            } else {
                current.id
            }
        }.sortedBy { it.position }
        return ObjectData2(
            id = current.id,
            page = current.page,
            position = current.position,
            name = dataSource.name,
            showAlreadyWatchedLine = dataSource.showAlreadyWatchedLine,
            showIco = current.showIco,
            childsShowName = current.childsShowName,
            childsNamePosition = current.childsNamePosition,
            childsShowAlreadyWatchedLine = current.childsShowAlreadyWatchedLine,
            childsShowAuthor = current.childsShowAuthor,
            image = dataSource.image,
            description = dataSource.description,
            author = dataSource.author,
            type = dataSource.type,
            alreadyWatched = dataSource.alreadyWatched,
            length = dataSource.length,
            carouselType = dataSource.carouselType,
            carouselCollectionType = dataSource.carouselCollectionType,
            childsSize = current.childsSize,
            childsCornerRadius = current.childsCornerRadius,
            layoutType = dataSource.layoutType,
            dovodchik = current.dovodchik,
            showDovodchikDots = current.showDovodchikDots,
            objectsInOneLine = current.objectsInOneLine,
            maxLines = current.maxLines,
            adaptiveGridSize = current.adaptiveGridSize,
            maxObjectsInOneLineForAdaptiveSize = current.maxObjectsInOneLineForAdaptiveSize,
            maxLinesForAdaptiveSize = current.maxLinesForAdaptiveSize,
            childs = childs.map { build(it, allItems) },
            link = current.link,
            elementType = dataSource.elementType,
            genre = dataSource.genre
        )
    }

    suspend fun buildObject(id: Long): ObjectData2? {
        val item = dao.getById(id)
        if (item != null) {
            val childs = dao.getChilds(item.id)
            val newChilds = mutableListOf<ObjectData2>()

            childs.forEach {
                val child = buildObject(it.id)
                if (child != null) {
                    newChilds.add(child)
                }
            }

            return ObjectData2(
                id = item.id,
                page = item.page,
                position = item.position,
                name = item.name,
                showAlreadyWatchedLine = item.showAlreadyWatchedLine,
                showIco = item.showIco,
                childsShowName = item.childsShowName,
                childsNamePosition = item.childsNamePosition,
                childsShowAlreadyWatchedLine = item.childsShowAlreadyWatchedLine,
                childsShowAuthor = item.childsShowAuthor,
                image = item.image,
                description = item.description,
                author = item.author,
                type = item.type,
                alreadyWatched = item.alreadyWatched,
                length = item.length,
                carouselType = item.carouselType,
                carouselCollectionType = item.carouselCollectionType,
                childsSize = item.childsSize,
                childsCornerRadius = item.childsCornerRadius,
                layoutType = item.layoutType,
                dovodchik = item.dovodchik,
                showDovodchikDots = item.showDovodchikDots,
                objectsInOneLine = item.objectsInOneLine,
                maxLines = item.maxLines,
                adaptiveGridSize = item.adaptiveGridSize,
                maxObjectsInOneLineForAdaptiveSize = item.maxObjectsInOneLineForAdaptiveSize,
                maxLinesForAdaptiveSize = item.maxLinesForAdaptiveSize,
                childs = newChilds,
                link = item.link,
                elementType = item.elementType,
                genre = item.genre,
            )
        }

        return null
    }

    suspend fun getParentCard(card: ObjectData2): ObjectData2 {
        return resolveParentRecursively(
            currentCard = card,
            visitedIds = mutableSetOf(card.id),
            depth = 0
        )
    }

    private tailrec suspend fun resolveParentRecursively(
        currentCard: ObjectData2,
        visitedIds: MutableSet<Long>,
        depth: Int,
        maxDepth: Int = 20
    ): ObjectData2 {
        val link = currentCard.link

        if (link == null || link.type != LinkType.INSERT || link.targetId == null || depth >= maxDepth) {
            return currentCard
        }

        val targetId = link.targetId

        if (visitedIds.contains(targetId)) {
            return currentCard
        }

        val targetCard = buildObject(targetId) ?: return currentCard

        visitedIds.add(targetId)

        return resolveParentRecursively(
            currentCard = targetCard,
            visitedIds = visitedIds,
            depth = depth + 1,
            maxDepth = maxDepth
        )
    }

    fun insert() {
        viewModelScope.launch(Dispatchers.IO) {
            val cr1 = ObjectData(
                id = 1,
                page = PageType.Home,
                parentId = null,
                position = 0,
                name = "Любимые аниме",
                showAlreadyWatchedLine = true,
                showIco = false,
                childsShowName = true,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = true,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsSize = CardSize.SMALL,
                childsCornerRadius = SizeType.MEDIUM,
                layoutType = LayoutType.DEFAULT,
                dovodchik = true,
                showDovodchikDots = true,
                objectsInOneLine = null,
                maxLines = null,
                link = null,
                elementType = ElementType.Carousel,
                genre = null,
                carouselType = CarouselType.AnimeNManga
            )
            dao.insert(cr1)
            val cd1 = ObjectData(
                id = 2,
                page = PageType.Home,
                parentId = 1,
                position = 1,
                name = "Звёздное дитя 3",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                description = null,
                author = null,
                type = null,
                alreadyWatched = 32,
                length = 50,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.SELF,
                    targetId = null,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = listOf(
                    Genre.Drama,
                    Genre.Detective,
                    Genre.Shonen
                )
            )
            dao.insert(cd1)
            val cd2 = ObjectData(
                id = 3,
                page = PageType.Home,
                parentId = 1,
                position = 2,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd2)
            val cd3 = ObjectData(
                id = 4,
                page = PageType.Home,
                parentId = 1,
                position = 3,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd3)
            val cd4 = ObjectData(
                id = 5,
                page = PageType.Home,
                parentId = 1,
                position = 4,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd4)
            val cd5 = ObjectData(
                id = 6,
                page = PageType.Home,
                parentId = 1,
                position = 5,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd5)
            val cd6 = ObjectData(
                id = 7,
                page = PageType.Home,
                parentId = 1,
                position = 6,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd6)

            val cr2 = ObjectData(
                id = 8,
                page = PageType.Home,
                parentId = null,
                position = 0,
                name = "Любимые аниме",
                showAlreadyWatchedLine = true,
                showIco = false,
                childsShowName = true,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = true,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsSize = CardSize.SMALL,
                childsCornerRadius = SizeType.MEDIUM,
                layoutType = LayoutType.CAROUSEL_FROM_GRID,
                dovodchik = true,
                showDovodchikDots = true,
                objectsInOneLine = 3,
                maxLines = 1,
                link = null,
                elementType = ElementType.Carousel,
                genre = null,
                carouselType = CarouselType.AnimeNManga
            )
            dao.insert(cr2)
            val cd7 = ObjectData(
                id = 9,
                page = PageType.Home,
                parentId = 8,
                position = 1,
                name = "Звёздное дитя 3",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                description = null,
                author = null,
                type = null,
                alreadyWatched = 32,
                length = 50,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.SELF,
                    targetId = null,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = listOf(
                    Genre.Drama,
                    Genre.Detective,
                    Genre.Shonen
                )
            )
            dao.insert(cd7)
            val cd8 = ObjectData(
                id = 10,
                page = PageType.Home,
                parentId = 8,
                position = 2,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd8)
            val cd9 = ObjectData(
                id = 11,
                page = PageType.Home,
                parentId = 8,
                position = 3,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd9)
            val cd10 = ObjectData(
                id = 12,
                page = PageType.Home,
                parentId = 8,
                position = 4,
                name = "Звёздное дитя",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd10)
            val cd11 = ObjectData(
                id = 13,
                page = PageType.Home,
                parentId = 8,
                position = 5,
                name = "Звёздное дитя",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd11)
            val cd12 = ObjectData(
                id = 14,
                page = PageType.Home,
                parentId = 8,
                position = 6,
                name = "Звёздное дитя",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.DEFAULT,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd12)

            val cr3 = ObjectData(
                id = 15,
                page = PageType.Home,
                parentId = null,
                position = 0,
                name = "Любимые аниме",
                showAlreadyWatchedLine = true,
                showIco = false,
                childsShowName = true,
                childsNamePosition = 0,
                childsShowAuthor = true,
                childsShowAlreadyWatchedLine = true,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsSize = CardSize.SMALL,
                childsCornerRadius = SizeType.MEDIUM,
                layoutType = LayoutType.CAROUSEL_FROM_FLAT_GRID,
                dovodchik = true,
                showDovodchikDots = true,
                objectsInOneLine = 1,
                maxLines = 3,
                link = null,
                elementType = ElementType.Carousel,
                genre = null,
                carouselType = CarouselType.AnimeNManga
            )
            dao.insert(cr3)
            val cd13 = ObjectData(
                id = 16,
                page = PageType.Home,
                parentId = 15,
                position = 1,
                name = "Звёздное дитя 3",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = ImageData.Url("https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"),
                description = null,
                author = null,
                type = null,
                alreadyWatched = 32,
                length = 50,
                childsCornerRadius = null,
                layoutType = LayoutType.FLAT_GRID_ITEM,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.SELF,
                    targetId = null,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = listOf(
                    Genre.Drama,
                    Genre.Detective,
                    Genre.Shonen
                )
            )
            dao.insert(cd13)
            val cd14 = ObjectData(
                id = 17,
                page = PageType.Home,
                parentId = 15,
                position = 2,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.FLAT_GRID_ITEM,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 16,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd14)
            val cd15 = ObjectData(
                id = 18,
                page = PageType.Home,
                parentId = 15,
                position = 3,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.FLAT_GRID_ITEM,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 16,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd15)
            val cd16 = ObjectData(
                id = 19,
                page = PageType.Home,
                parentId = 15,
                position = 4,
                name = "Звёздное дитя 3 222",
                showAlreadyWatchedLine = false,
                showIco = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                childsCornerRadius = null,
                layoutType = LayoutType.FLAT_GRID_ITEM,
                dovodchik = false,
                showDovodchikDots = false,
                objectsInOneLine = null,
                maxLines = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 16,
                    contentPath = null
                ),
                elementType = ElementType.AnimeCard,
                genre = null
            )
            dao.insert(cd16)

            groupDao.deleteAll()
            groupDao.insert(GroupsData(0, 0, 2, 0))
            groupDao.insert(GroupsData(0, 0, 3, 1))
        }

    }

    suspend fun insertCarousel(objectData: ObjectData, page: PageType): Long {
        val position = dao.getMaxPositionOnPage(null, page) ?: -1
        objectData.position = position + 1
        objectData.page = page
        val carouselId = dao.insert(objectData)
        return carouselId
    }

    suspend fun editObject(objectData: ObjectData) {
        val position = dao.getPositionById(objectData.id)
        if (position != null) {
            objectData.position = position
            dao.updateObject(objectData)
        }
    }

    suspend fun insertCard(objectData: ObjectData, parentId: Long): Long {
        val position = dao.getMaxPosition(parentId) ?: -1
        objectData.position = position + 1
        objectData.parentId = parentId
        val cardId = dao.insert(objectData)
        return cardId
    }

    suspend fun insertEpisode(episodeInfo: EpisodeInfo, parentId: Long): Long {
        val position = dao.getMaxPosition(parentId) ?: -1
        val data = ObjectData(
            id = 0,
            page = PageType.Home,
            parentId = parentId,
            position = position + 1,
            name = episodeInfo.name,
            showAlreadyWatchedLine = false,
            showIco = false,
            childsShowName = false,
            childsNamePosition = 0,
            childsShowAlreadyWatchedLine = false,
            image = episodeInfo.image,
            description = null,
            author = null,
            type = null,
            alreadyWatched = 0,
            length = episodeInfo.length,
            childsCornerRadius = null,
            layoutType = LayoutType.DEFAULT,
            dovodchik = false,
            showDovodchikDots = false,
            objectsInOneLine = null,
            maxLines = null,
            link = episodeInfo.link,
            elementType = ElementType.Episode,
            genre = null
        )
        val episodeId = dao.insert(data)
        return episodeId
    }

    suspend fun insertChapter(chapterInfo: ChapterInfo, parentId: Long): Long {
        val position = dao.getMaxPosition(parentId) ?: -1
        val data = ObjectData(
            id = 0,
            page = PageType.Home,
            parentId = parentId,
            position = position + 1,
            name = chapterInfo.name,
            alreadyWatched = 0,
            length = chapterInfo.childs.size.toLong(),
            link = LinkData(LinkType.SELF, null, null),
            elementType = ElementType.Chapter,
        )
        val chapterId = dao.insert(data)
        val cd = chapterInfo.childs
        for ((i, element) in cd.withIndex()) {
            val chapterPageData = ObjectData(
                id = 0,
                page = PageType.Home,
                parentId = chapterId,
                position = i,
                image = null,
                type = null,
                alreadyWatched = 0,
                length = 1L,
                link = element.link,
                elementType = ElementType.ChapterPage,
            )
            dao.insert(chapterPageData)
        }
        return chapterId
    }

    fun insertCardWithEpisodes(
        cardInfo: ObjectData,
        episodesInfo: List<EpisodeInfo>,
        parentId: Long
    ) {
        viewModelScope.launch {
            val cardId = insertCard(cardInfo, parentId)
            for (i in episodesInfo.indices) {
                insertEpisode(episodesInfo[i], cardId)
            }
        }
    }

    fun insertCardWithChapters(
        cardInfo: ObjectData,
        chaptersInfo: List<ChapterInfo>,
        parentId: Long
    ) {
        viewModelScope.launch {
            val cardId = insertCard(cardInfo, parentId)
            for (i in chaptersInfo.indices) {
                insertChapter(chaptersInfo[i], cardId)
            }
        }
    }

    fun insertMusicCard(
        cardInfo: ObjectData,
        parentId: Long,
        song: LinkData?,
        verticalVideo: LinkData?,
        horizontalVideo: LinkData?
    ) {
        viewModelScope.launch {
            val cardId = insertCard(cardInfo, parentId)
            if (song != null) {
                val songData = ObjectData(
                    id = 0,
                    page = PageType.Home,
                    parentId = cardId,
                    position = 0,
                    link = song,
                    elementType = ElementType.Song,
                    length = 0L,
                    alreadyWatched = 0L,
                )
                dao.insert(songData)
            }
            if (verticalVideo != null) {
                val videoData = ObjectData(
                    id = 0,
                    page = PageType.Home,
                    parentId = cardId,
                    position = 1,
                    link = verticalVideo,
                    elementType = ElementType.SongVerticalVideo,
                    length = 0L,
                    alreadyWatched = 0L,
                )
                dao.insert(videoData)
            }
            if (horizontalVideo != null) {
                val videoData = ObjectData(
                    id = 0,
                    page = PageType.Home,
                    parentId = cardId,
                    position = 2,
                    link = horizontalVideo,
                    elementType = ElementType.SongHorizontalVideo,
                    length = 0L,
                    alreadyWatched = 0L
                )
                dao.insert(videoData)
            }
        }
    }
}