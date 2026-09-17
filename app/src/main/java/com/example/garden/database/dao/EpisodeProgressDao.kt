package com.example.garden.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.garden.database.EpisodeProgressEntity
import com.example.garden.database.LinkData
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeProgressDao {

    @Upsert
    suspend fun saveProgress(progress: EpisodeProgressEntity)

    @Query("SELECT * FROM episode_progress WHERE link = :link LIMIT 1")
    fun getProgressByLink(link: LinkData): Flow<EpisodeProgressEntity?>

    @Query("SELECT * FROM episode_progress WHERE link = :link LIMIT 1")
    suspend fun getProgressByLinkDirect(link: LinkData): EpisodeProgressEntity?

    @Query("SELECT * FROM episode_progress WHERE link IN (:links)")
    fun getProgressForLinks(links: List<LinkData>): Flow<List<EpisodeProgressEntity>>

    @Query("SELECT * FROM episode_progress WHERE positionMs > 0 ORDER BY lastWatchedAt DESC")
    fun getAllWatchedProgress(): Flow<List<EpisodeProgressEntity>>

    @Query("DELETE FROM episode_progress WHERE link = :link")
    suspend fun deleteProgress(link: LinkData)

    @Query("DELETE FROM episode_progress")
    suspend fun clearAll()
}
