package com.example.garden.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

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

    @Query("SELECT MAX(position) FROM objectData WHERE (:parentId IS NULL AND parentId IS NULL) OR parentId = :parentId")
    suspend fun getMaxPosition(parentId: Long?): Int?
    @Query("SELECT MAX(position) FROM objectData WHERE ((:parentId IS NULL AND parentId IS NULL) OR parentId = :parentId) AND page = :page")
    suspend fun getMaxPositionOnPage(parentId: Long?, page: Int): Int?
    @Query("SELECT * FROM objectData WHERE id = :id")
    suspend fun getById(id: Long): ObjectData?

    @Query("DELETE FROM objectData")
    suspend fun deleteAll()
}