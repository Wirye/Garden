package com.example.garden.repository.webObjects

import com.example.garden.database.dao.WebObjectDataDao
import com.example.garden.database.entities.CollectionType
import com.example.garden.database.entities.WebObjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WebObjectsRepositoryImpl(
    private val dao: WebObjectDataDao
) : WebObjectsRepository {
    override fun getAllChildsWithCollectionType(): Flow<Map<CollectionType, List<WebObjectEntity>>> =
        dao.getAllChildsWithCollectionType().map { list ->
            list.groupBy { it.carouselCollectionType }
        }
}
