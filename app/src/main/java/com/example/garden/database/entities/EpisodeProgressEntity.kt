package com.example.garden.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "episode_progress",
)
data class EpisodeProgressEntity(
    @PrimaryKey(autoGenerate = false) val link: LinkData,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val isCompleted: Boolean = false,
    val lastWatchedAt: Long = System.currentTimeMillis()
)
