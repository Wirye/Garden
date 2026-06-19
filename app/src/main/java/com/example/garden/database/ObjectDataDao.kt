package com.example.garden.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectDataDao {
    @Query("SELECT * FROM objectData")
    fun getAll(): Flow<List<ObjectData>>
    // Найти элементы конкретной страницы (например, только Home)
    @Query("SELECT * FROM objectData WHERE page = :pageId AND parentId IS NULL ORDER BY position ASC")
    suspend fun getCarouselsForPage(pageId: Int): List<ObjectData>

    // Найти все карточки внутри конкретной карусели
    @Query("SELECT * FROM objectData WHERE parentId = :parentId ORDER BY position ASC")
    suspend fun getChilds(parentId: Long): List<ObjectData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(objectData: ObjectData): Long

    @Query("UPDATE objectData SET alreadyWatched = :alreadyWatched WHERE id = :id")
    suspend fun editAlreadyWatched(id: Long, alreadyWatched: Long)

    @Query("SELECT MAX(position) FROM objectData WHERE parentId = :parentId")
    suspend fun getMaxPosition(parentId: Long?): Int?
    @Query("SELECT * FROM objectData WHERE id = :id")
    suspend fun getById(id: Long): ObjectData?

    @Query("DELETE FROM objectData")
    suspend fun deleteAll()
}