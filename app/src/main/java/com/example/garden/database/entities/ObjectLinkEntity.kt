package com.example.garden.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "object_links",
    primaryKeys = ["objectId", "targetId"],
    foreignKeys = [
        ForeignKey(
            entity = ObjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["objectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ObjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["targetId"])]
)
data class ObjectLinkEntity(
    val objectId: Long,
    val targetId: Long
)
