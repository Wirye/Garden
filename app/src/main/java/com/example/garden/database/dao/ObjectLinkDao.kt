package com.example.garden.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.garden.database.entities.ObjectLinkEntity
import com.example.garden.database.entities.ObjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectLinkDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(link: ObjectLinkEntity): Long   // -1, если такая связь уже есть

    @Query("SELECT EXISTS(SELECT 1 FROM object_links WHERE objectId = :targetId AND targetId = :objectId)")
    suspend fun reverseLinkExists(objectId: Long, targetId: Long): Boolean

    /** true, если связь добавлена */
    @Transaction
    suspend fun addLink(objectId: Long, targetId: Long): Boolean {
        if (objectId == targetId) return false                 // связь на самого себя
        if (reverseLinkExists(objectId, targetId)) return false // обратная связь
        return insertLink(ObjectLinkEntity(objectId, targetId)) != -1L
    }

    @Query("DELETE FROM object_links WHERE objectId = :objectId AND targetId = :targetId")
    suspend fun removeLink(objectId: Long, targetId: Long)

    // Все связанные объекты (в обе стороны)
    @Query("""
        SELECT o.* FROM objectData o
        WHERE o.id IN (
            SELECT targetId FROM object_links WHERE objectId = :id
            UNION
            SELECT objectId FROM object_links WHERE targetId = :id
        )
    """)
    fun getRelatedObjects(id: Long): Flow<List<ObjectEntity>>

    @Query("""
        SELECT targetId FROM object_links WHERE objectId = :id
        UNION
        SELECT objectId FROM object_links WHERE targetId = :id
    """)
    fun getRelatedObjectIds(id: Long): Flow<List<Long>>

    // Валидные: на них ссылаются, а сами они ни на кого не ссылаются
    @Query("""
        SELECT o.* FROM objectData o
        WHERE EXISTS (SELECT 1 FROM object_links l WHERE l.targetId = o.id)
          AND NOT EXISTS (SELECT 1 FROM object_links l WHERE l.objectId = o.id)
    """)
    fun getValidObjects(): Flow<List<ObjectEntity>>
}
