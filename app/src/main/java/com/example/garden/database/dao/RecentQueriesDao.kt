package com.example.garden.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.garden.database.entities.RecentQueriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentQueriesDao {
    @Query("SELECT * FROM recentQueries ORDER BY position ASC LIMIT 15")
    fun getRecentQueries(): Flow<List<RecentQueriesEntity>>

    @Upsert
    suspend fun upsertRecentQuery(entity: RecentQueriesEntity)

    @Query("UPDATE recentQueries SET position = position - 1 WHERE position > :position")
    suspend fun shiftPositions(position: Int)

    @Delete
    suspend fun deleteByEntity(entity: RecentQueriesEntity)

    @Query("DELETE FROM recentQueries WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM recentQueries WHERE position = :position")
    suspend fun deleteByPosition(position: Int)

    @Transaction
    suspend fun deleteRecentQuery(entity: RecentQueriesEntity) {
        deleteByEntity(entity)
        deleteByQuery(entity.query)
        deleteByPosition(entity.position)
        shiftPositions(entity.position)
    }

    @Query("DELETE FROM recentQueries WHERE position > 15")
    suspend fun deleteUnnecessaryRecentQueries()

    @Query("UPDATE recentQueries SET position = position + 1")
    suspend fun increasePositions()

    @Query("SELECT * FROM recentQueries WHERE `query` = :query")
    suspend fun getByQuery(query: String): RecentQueriesEntity?

    @Transaction
    suspend fun addOrUpRecentQuery(entity: RecentQueriesEntity) {
        increasePositions()
        val pos: Int? = getByQuery(entity.query)?.position
        upsertRecentQuery(entity)
        if (pos != null) {
            shiftPositions(pos)
        }
        deleteUnnecessaryRecentQueries()
    }
}
