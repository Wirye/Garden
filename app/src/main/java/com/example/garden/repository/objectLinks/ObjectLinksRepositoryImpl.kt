package com.example.garden.repository.objectLinks

import com.example.garden.database.dao.ObjectLinkDao

class ObjectLinksRepositoryImpl(private val dao: ObjectLinkDao) : ObjectLinksRepository {
    override suspend fun addLink(objectId: Long, targetId: Long) = dao.addLink(objectId, targetId)
    override suspend fun removeLink(objectId: Long, targetId: Long) = dao.removeLink(objectId, targetId)
    override fun getRelatedObjects(id: Long) = dao.getRelatedObjects(id)
    override fun getRelatedObjectIds(id: Long) = dao.getRelatedObjectIds(id)
    override fun getValidObjects() = dao.getValidObjects()
}
