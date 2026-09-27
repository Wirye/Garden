package com.example.garden.repository.recentQueries

import com.example.garden.database.entities.RecentQueriesEntity
import kotlinx.coroutines.flow.Flow

interface RecentQueriesRepository {
    fun getRecentQueries(): Flow<List<RecentQueriesEntity>>
    suspend fun addOrUpRecentQuery(entity: RecentQueriesEntity)
    suspend fun deleteRecentQuery(entity: RecentQueriesEntity)
}