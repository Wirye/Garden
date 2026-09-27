package com.example.garden.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "webObjectData")
data class WebObjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentCollectionType: CollectionType? = null,
    val name: String,
    val position: Int,
    val carouselCollectionType: CollectionType,
    val elementType: ElementType,
    val info: ObjectData
)
