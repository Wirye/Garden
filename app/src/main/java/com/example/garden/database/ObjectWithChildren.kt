package com.example.garden.database

import androidx.room.Embedded
import androidx.room.Relation

data class ObjectWithChilds(
    @Embedded
    val parent: ObjectEntity,

    @Relation(
        entity = ResolvedObjectEntity::class,
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val childs: List<ObjectEntity>
)

data class ObjectWithChilds2(
    val parent: ObjectData.Carousel,
    val childs: List<ObjectData.Card>
)
