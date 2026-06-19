package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garden.appsettings.AnimeSettingsState
import com.example.garden.appsettings.SettingsManager
import com.example.garden.baseDensity
import com.example.garden.database.ElementType
import com.example.garden.database.Genre
import com.example.garden.database.ImageData
import com.example.garden.database.ImageSource
import com.example.garden.database.LinkData
import com.example.garden.database.LinkType
import com.example.garden.database.groups.groupsData
import com.example.garden.database.groups.groupsDataDao
import com.example.garden.database.objectData
import com.example.garden.database.objectDataDao
import com.example.garden.episodeInfo
import com.example.garden.findObjectByIdInList
import com.example.garden.objectData2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.round

data class animePageUIModel(
    val animeData: objectData2?,
    val groups: List<groupsData>,
    val settingsState: AnimeSettingsState
)

class MainViewModel(private val dao: objectDataDao, private val groupDao: groupsDataDao, private val settingsManager: SettingsManager, private val baseDensity1: Float) : ViewModel() {
    val uiDataFlow: Flow<List<objectData2>> = dao.getAll().map {
        allItems ->
        val roots = allItems.filter { it.parentId == null }.sortedBy { it.position }
        roots.map { build(it,allItems) }
    }
    private val _animePageObjectFlow = MutableStateFlow<objectData2?>(null)

    val groupsFlow: Flow<List<groupsData>> = groupDao.getAll()
    val settingsStateFlow = settingsManager.settingsStateFlow

    val animePageFlow = combine(
        _animePageObjectFlow,
        groupsFlow,
        settingsStateFlow
    ) {
        animePageObject, groups, settingsState ->
        animePageUIModel(
            animeData = animePageObject,
            groups = groups,
            settingsState = AnimeSettingsState(settingsState.showChildsName, settingsState.childsNamePosition)
        )
    }

    fun toggleShowNames(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setShowChildsName(isEnabled)
        }
    }

    fun getGroupById(id: Long, list: List<groupsData>): Int? {
        var res: Int? = null
        for (i in 0 until list.size) {
            val obj = list[i]
            if (obj.id == id) {
                res = obj.groupNumber
                break
            }
        }
        return res
    }

    fun getGroupObjectsByGroupNumber(groupNumber: Int, list: List<groupsData>): List<groupsData> {
        val res = mutableListOf<groupsData>()
        for (i in 0 until list.size) {
            val obj = list[i]
            if (obj.groupNumber == groupNumber) {
                res.add(obj)
            }
        }
        res.sortBy { it.position }
        return res
    }

    fun getGroupObjectsByIdOfOne(id: Long, list: List<objectData2>, list2: List<groupsData>): List<objectData2> {
        val groupNumber = getGroupById(id, list2)
        val groupObjects = mutableListOf<objectData2>()
        if (groupNumber != null) {
            val groupObjectsIds = getGroupObjectsByGroupNumber(groupNumber, list2)
            for (i in groupObjectsIds) {
                val obj = findObjectByIdInList(i.id, list)
                if (obj != null && i.id != id) {
                    groupObjects.add(obj)
                }
            }
        }
        groupObjects.sortBy { it.position }
        for (i in 0 until groupObjects.size) {
            groupObjects[i].position = i
        }
        return groupObjects
    }

    private fun build(current: objectData, allItems: List<objectData>): objectData2 {
        val dataSource = if (current.link?.type == LinkType.INSERT && current.link?.targetId != null) { allItems.find { it.id == current.link?.targetId } ?: current } else { current }
        val childs = allItems.filter { it.parentId == if (dataSource != current) {dataSource.id} else {current.id } }.sortedBy { it.position }
        return objectData2(
            id = current.id,
            page = current.page,
            position = current.position,
            name = dataSource.name,
            showName = dataSource.showName,
            namePosition = dataSource.namePosition,
            showAlreadyWatchedLine = dataSource.showAlreadyWatchedLine,
            showAvatar = current.showAvatar,
            childsShowName = current.childsShowName,
            childsNamePosition = current.childsNamePosition,
            childsShowAlreadyWatchedLine = current.childsShowAlreadyWatchedLine,
            image = dataSource.image,
            description = dataSource.description,
            author = dataSource.author,
            type = dataSource.type,
            alreadyWatched = dataSource.alreadyWatched,
            length = dataSource.length,
            width = if (current.width != null) {round(current.width!!.toFloat() * baseDensity1).toInt()} else {null},
            height = if (current.height != null) {round(current.height!!.toFloat() * baseDensity1).toInt()} else {null},
            childsCornerRadius = current.childsCornerRadius,
            layoutType = dataSource.layoutType,
            dovodchik = current.dovodchik,
            showDovodchikDots = current.showDovodchikDots,
            maxObjectsInOneLine = current.maxObjectsInOneLine,
            maxLines = current.maxLines,
            paddingHorizontal = if (current.paddingHorizontal != null) {round(current.paddingHorizontal!!.toFloat() * baseDensity1).toInt()} else {null},
            paddingVertical = if (current.paddingVertical != null) {round(current.paddingVertical!!.toFloat() * baseDensity1).toInt()} else {null},
            marginBetweenElementsHorizontal = if (current.marginBetweenElementsHorizontal != null) {round(current.marginBetweenElementsHorizontal!!.toFloat() * baseDensity1).toInt()} else {null},
            marginBetweenElementsVertical = if (current.marginBetweenElementsVertical != null) {round(current.marginBetweenElementsVertical!!.toFloat() * baseDensity1).toInt()} else {null},
            childs = childs.map { build(it,allItems) },
            link = dataSource.link,
            elementType = dataSource.elementType,
            genre = dataSource.genre
        )
    }
    fun insert() {
        viewModelScope.launch(Dispatchers.IO) {
            val cr1 = objectData(
                id = 1,
                page = 0,
                parentId = null,
                position = 0,
                name = "Любимые аниме",
                showName = true,
                namePosition = 0,
                showAlreadyWatchedLine = true,
                showAvatar = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = true,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                width = null,
                height = null,
                childsCornerRadius = null,
                layoutType = null,
                dovodchik = true,
                showDovodchikDots = true,
                maxObjectsInOneLine = null,
                maxLines = null,
                paddingHorizontal = null,
                paddingVertical = null,
                marginBetweenElementsHorizontal = null,
                marginBetweenElementsVertical = null,
                link = null,
                elementType = ElementType.Carousel,
                genre = null
            )
            dao.insert(cr1)
            val cd1 = objectData(
                id = 2,
                page = 0,
                parentId = 1,
                position = 0,
                name = "Звёздное дитя 3",
                showName = true,
                namePosition = 0,
                showAlreadyWatchedLine = false,
                showAvatar = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = ImageData(
                    source = ImageSource.URL,
                    value = "https://anilibria.top/storage/releases/posters/10089/PKg3Ru0WTMgTSSXhIpJICXjdE5DNvvLE.webp"
                ),
                description = null,
                author = null,
                type = null,
                alreadyWatched = 32,
                length = 50,
                width = round(520f / baseDensity1).toInt(),
                height = round(743f / baseDensity1).toInt(),
                childsCornerRadius = null,
                layoutType = null,
                dovodchik = false,
                showDovodchikDots = false,
                maxObjectsInOneLine = null,
                maxLines = null,
                paddingHorizontal = null,
                paddingVertical = null,
                marginBetweenElementsHorizontal = null,
                marginBetweenElementsVertical = null,
                link = LinkData(
                    type = LinkType.SELF,
                    targetId = null,
                    contentPath = null
                ),
                elementType = ElementType.Anime,
                genre = listOf(
                    Genre.Drama,
                    Genre.Detective,
                    Genre.Shonen
                )
            )
            dao.insert(cd1)
            val cd2 = objectData(
                id = 3,
                page = 0,
                parentId = 1,
                position = 1,
                name = "Звёздное дитя 3 222",
                showName = true,
                namePosition = 0,
                showAlreadyWatchedLine = false,
                showAvatar = false,
                childsShowName = false,
                childsNamePosition = 0,
                childsShowAlreadyWatchedLine = false,
                image = null,
                description = null,
                author = null,
                type = null,
                alreadyWatched = 0,
                length = 0,
                width = round(520f / baseDensity1).toInt(),
                height = round(743f / baseDensity1).toInt(),
                childsCornerRadius = null,
                layoutType = null,
                dovodchik = false,
                showDovodchikDots = false,
                maxObjectsInOneLine = null,
                maxLines = null,
                paddingHorizontal = null,
                paddingVertical = null,
                marginBetweenElementsHorizontal = null,
                marginBetweenElementsVertical = null,
                link = LinkData(
                    type = LinkType.INSERT,
                    targetId = 2,
                    contentPath = null
                ),
                elementType = ElementType.Music,
                genre = null
            )
            dao.insert(cd2)
            groupDao.deleteAll()
            groupDao.insert(groupsData(0,0, 2, 0))
            groupDao.insert(groupsData(0,0,3,1))
        }

    }

    suspend fun insertCarousel(objectData: objectData, page: Int): Long {
        val position = dao.getMaxPosition(null) ?: -1
        objectData.position = position+1
        objectData.page = page
        objectData.paddingHorizontal = if (objectData.paddingHorizontal != null) {round(objectData.paddingHorizontal!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.paddingVertical = if (objectData.paddingVertical != null) {round(objectData.paddingVertical!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.marginBetweenElementsHorizontal = if (objectData.marginBetweenElementsHorizontal != null) {round(objectData.marginBetweenElementsHorizontal!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.marginBetweenElementsVertical = if (objectData.marginBetweenElementsVertical != null) {round(objectData.marginBetweenElementsVertical!!.toFloat() / baseDensity1).toInt()} else {null}
        val carouselId = dao.insert(objectData)
        return carouselId
    }

    suspend fun insertCard(objectData: objectData, parentId: Long): Long {
        val position = dao.getMaxPosition(parentId) ?: -1
        objectData.position = position+1
        objectData.parentId = parentId
        objectData.width = if (objectData.width != null) {round(objectData.width!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.height = if (objectData.height != null) {round(objectData.height!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.paddingHorizontal = if (objectData.paddingHorizontal != null) {round(objectData.paddingHorizontal!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.paddingVertical = if (objectData.paddingVertical != null) {round(objectData.paddingVertical!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.marginBetweenElementsHorizontal = if (objectData.marginBetweenElementsHorizontal != null) {round(objectData.marginBetweenElementsHorizontal!!.toFloat() / baseDensity1).toInt()} else {null}
        objectData.marginBetweenElementsVertical = if (objectData.marginBetweenElementsVertical != null) {round(objectData.marginBetweenElementsVertical!!.toFloat() / baseDensity1).toInt()} else {null}
        val cardId = dao.insert(objectData)
        return cardId
    }

    suspend fun insertEpisode(episodeInfo: episodeInfo, parentId: Long): Long {
        val position = dao.getMaxPosition(parentId) ?: -1
        val data = objectData(
            id = 0,
            page = 0,
            parentId = parentId,
            position = position+1,
            name = episodeInfo.name,
            showName = true,
            namePosition = 0,
            showAlreadyWatchedLine = false,
            showAvatar = false,
            childsShowName = false,
            childsNamePosition = 0,
            childsShowAlreadyWatchedLine = false,
            image = episodeInfo.image,
            description = null,
            author = null,
            type = null,
            alreadyWatched = 0,
            length = episodeInfo.length,
            width = null,
            height = null,
            childsCornerRadius = null,
            layoutType = null,
            dovodchik = false,
            showDovodchikDots = false,
            maxObjectsInOneLine = null,
            maxLines = null,
            paddingHorizontal = null,
            paddingVertical = null,
            marginBetweenElementsHorizontal = null,
            marginBetweenElementsVertical = null,
            link = episodeInfo.link,
            elementType = ElementType.Episode,
            genre = null
        )
        val episodeId = dao.insert(data)
        return episodeId
    }
    fun insertCardWithEpisodes(cardInfo: objectData, episodesInfo: List<episodeInfo>, parentId: Long) {
        viewModelScope.launch {
            val cardId = insertCard(cardInfo, parentId)
            for (i in 0 until episodesInfo.size) {
                insertEpisode(episodesInfo[i], cardId)
            }
        }
    }
    fun editAlreadyWatched(id: Long, alreadyWatched: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.editAlreadyWatched(id, alreadyWatched)
        }
    }

    suspend fun getAllInfoByEpisodeId(id: Long): Triple<objectData?, List<objectData>, Int> {
        var parentCard: objectData? = null
        val episodesListToReturn = mutableListOf<objectData>()
        val thisEpisode = dao.getById(id)
        var thisEpisodePos = 0
        if (thisEpisode != null) {
            if (thisEpisode.parentId != null) {
                thisEpisodePos = thisEpisode.position
                parentCard = dao.getById(thisEpisode.parentId!!)
                if (parentCard != null) {
                    val episodes = dao.getChilds(parentCard.id)
                    episodesListToReturn.addAll(episodes)
                }
            }
        }
        if (parentCard != null) {
            parentCard.width = round(parentCard.width!!.toFloat() * baseDensity1).toInt()
            parentCard.height = round(parentCard.height!!.toFloat() * baseDensity1).toInt()
            parentCard.paddingVertical = round(parentCard.paddingVertical!!.toFloat() * baseDensity1).toInt()
            parentCard.paddingHorizontal = round(parentCard.paddingHorizontal!!.toFloat() * baseDensity1).toInt()
            parentCard.marginBetweenElementsVertical = round(parentCard.marginBetweenElementsVertical!!.toFloat() * baseDensity1).toInt()
            parentCard.marginBetweenElementsHorizontal = round(parentCard.marginBetweenElementsHorizontal!!.toFloat() * baseDensity1).toInt()
        }
        return Triple(parentCard,episodesListToReturn, thisEpisodePos)
    }
    fun setChildsShowNameInSezonsRecycler(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setShowChildsName(isEnabled)
        }
    }
    fun updateAnimePageAdapter(id: Long, list: List<objectData2>) = viewModelScope.launch(Dispatchers.Default) {
        val obj = findObjectByIdInList(id, list)
        _animePageObjectFlow.value = obj
    }
}