package com.example.garden.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recentQueries")
data class RecentQueriesEntity(
    @PrimaryKey(autoGenerate = false)
    val query: String,
    val position: Int
)
