package com.example.garden.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_groups",
    indices = [
        Index(value = ["groupId"]),
        Index(value = ["objectId"])
    ]
)
data class MediaGroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val objectId: Long,
    val positionInGroup: Int
)