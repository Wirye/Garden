package com.example.garden.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectDataDao {
    @Query("SELECT * FROM objectData")
    fun getAll(): Flow<List<ObjectData>>
    @Query("SELECT * FROM objectData WHERE page = :pageId AND parentId IS NULL ORDER BY position ASC")
    suspend fun getCarouselsForPage(pageId: PageType): List<ObjectData>

    @Query("SELECT * FROM objectData WHERE parentId = :parentId ORDER BY position ASC")
    suspend fun getChilds(parentId: Long): List<ObjectData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(objectData: ObjectData): Long

    @Query("UPDATE objectData SET alreadyWatched = :alreadyWatched WHERE id = :id")
    suspend fun editAlreadyWatched(id: Long, alreadyWatched: Long)

    @Query("SELECT MAX(position) FROM objectData WHERE (:parentId IS NULL AND parentId IS NULL) OR parentId = :parentId")
    suspend fun getMaxPosition(parentId: Long?): Int?
    @Query("SELECT MAX(position) FROM objectData WHERE ((:parentId IS NULL AND parentId IS NULL) OR parentId = :parentId) AND page = :page")
    suspend fun getMaxPositionOnPage(parentId: Long?, page: PageType): Int?
    @Query("SELECT * FROM objectData WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ObjectData?

    @Query("SELECT position FROM objectData WHERE id = :id")
    suspend fun getPositionById(id: Long): Int?

    @Update
    suspend fun updateObject(carousel: ObjectData)

    @Query("DELETE FROM objectData")
    suspend fun deleteAll()
}