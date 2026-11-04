package com.example.garden.repository.objectLinks

import com.example.garden.database.entities.ObjectEntity
import kotlinx.coroutines.flow.Flow

interface ObjectLinksRepository {
    suspend fun addLink(objectId: Long, targetId: Long): Boolean
    suspend fun removeLink(objectId: Long, targetId: Long)
    fun getRelatedObjects(id: Long): Flow<List<ObjectEntity>>
    fun getRelatedObjectIds(id: Long): Flow<List<Long>>
    fun getValidObjects(): Flow<List<ObjectEntity>>
}
