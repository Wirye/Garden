package com.example.garden.repository.webObjects

import com.example.garden.database.entities.CollectionType
import com.example.garden.database.entities.WebObjectEntity
import kotlinx.coroutines.flow.Flow

interface WebObjectsRepository {
    fun getAllChildsWithCollectionType(): Flow<Map< CollectionType, List<WebObjectEntity>>>
}