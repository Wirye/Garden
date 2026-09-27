package com.example.garden.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.garden.database.entities.CollectionType
import com.example.garden.database.entities.WebObjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WebObjectDataDao {
    @Query("SELECT * FROM webObjectData WHERE parentCollectionType IS :collectionType ORDER BY position ASC")
    fun getChildByCollectionType(collectionType: CollectionType): Flow<List<WebObjectEntity>>

    @Query("SELECT * FROM webObjectData WHERE parentCollectionType IS NOT NULL ORDER BY position ASC")
    fun getAllChildsWithCollectionType(): Flow<List<WebObjectEntity>>

    @Delete
    suspend fun deleteChild(entity: WebObjectEntity)

    @Upsert
    suspend fun upsertObject(entity: WebObjectEntity)

    @Query("DELETE FROM webObjectData WHERE parentCollectionType IS :collectionType")
    suspend fun deleteChildsByCollectionType(collectionType: CollectionType)

    @Query("DELETE FROM webObjectData WHERE carouselCollectionType IS :collectionType")
    suspend fun deleteCarouselsByCollectionType(collectionType: CollectionType)

    @Transaction
    suspend fun deleteByCollectionType(collectionType: CollectionType) {
        deleteChildsByCollectionType(collectionType)
        deleteCarouselsByCollectionType(collectionType)
    }
}
