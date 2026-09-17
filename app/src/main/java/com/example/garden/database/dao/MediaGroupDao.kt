package com.example.garden.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.garden.database.MediaGroupEntity
import com.example.garden.database.ObjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupItems(items: List<MediaGroupEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupItem(item: MediaGroupEntity)

    @Query("""
        SELECT o.* FROM objectData o
        INNER JOIN media_groups g ON o.id = g.objectId
        WHERE g.groupId = (
            SELECT groupId FROM media_groups WHERE objectId = :currentObjectId LIMIT 1
        ) 
        AND o.id != :currentObjectId
        ORDER BY g.positionInGroup ASC
    """)
    fun getRelatedObjects(currentObjectId: Long): Flow<List<ObjectEntity>>

    @Query("""
        SELECT objectId FROM media_groups 
        WHERE groupId = (
            SELECT groupId FROM media_groups WHERE objectId = :currentObjectId LIMIT 1
        )
        AND objectId != :currentObjectId
        ORDER BY positionInGroup ASC
    """)
    fun getRelatedObjectIds(currentObjectId: Long): Flow<List<Long>>

    @Query("DELETE FROM media_groups WHERE objectId = :objectId")
    suspend fun removeFromGroup(objectId: Long)

    @Query("DELETE FROM media_groups WHERE groupId = :groupId")
    suspend fun deleteGroup(groupId: Long)
}
