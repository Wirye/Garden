package com.example.garden.utils

import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.EntitySourceType
import com.example.garden.database.entities.ImageData
import com.example.garden.database.entities.LinkData
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.database.entities.PageType
import com.github.wirye.anilibriakt.model.Title

fun Title.toObjectEntity(position: Int, sourceType: EntitySourceType): ObjectEntity {
    return ObjectEntity(
        id = -1L,
        parentId = null,
        page = PageType.Home,
        position = position,
        elementType = ElementType.AnimeCard,
        link = LinkData.Self,
        isUserCreated = false,
        name = (this.name?.russian) ?: "",
        info = ObjectData.Card.AniLibria(
            id = "${sourceType.name}_-1",
            position = position,
            link = LinkData.Self,
            isUserCreated = false,
            name = (this.name?.russian) ?: "",
            author = "",
            image = ImageData.Url((this.poster?.fullPreviewUrl) ?: ""),
            anilibriaCardId = this.id,
            data = this,
        ),
        source = sourceType
    )
}
