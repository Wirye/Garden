package com.example.garden.repository.recentQueries

import com.example.garden.database.entities.RecentQueriesEntity
import com.example.garden.database.dao.RecentQueriesDao
import kotlinx.coroutines.flow.Flow

class RecentQueriesRepositoryImpl(private val dao: RecentQueriesDao) : RecentQueriesRepository {
    override fun getRecentQueries(): Flow<List<RecentQueriesEntity>> = dao.getRecentQueries()

    override suspend fun addOrUpRecentQuery(entity: RecentQueriesEntity) =
        dao.addOrUpRecentQuery(entity.copy(position = 0))

    override suspend fun deleteRecentQuery(entity: RecentQueriesEntity) = dao.deleteRecentQuery(entity)
}