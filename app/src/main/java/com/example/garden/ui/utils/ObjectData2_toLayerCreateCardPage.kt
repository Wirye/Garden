package com.example.garden.ui.utils

import com.example.garden.Layer
import com.example.garden.database.CarouselType
import com.example.garden.database.ObjectData2

fun ObjectData2.toLayerCreateCardPage(parentId: Long, carouselType: CarouselType) : Layer.CreateCardPage {
    return Layer.CreateCardPage(
        name = this.name ?: "",
        carouselType = carouselType,
        parentId = parentId,
        cardId = this.id,
        description = this.description ?: "",
        author = this.author ?: "",
        image = this.image,
        cardType = this.elementType,
        genreList = this.genre ?: emptyList(),
        episodesList = this.getEpisodesList(),
        chaptersList = this.getChaptersList(),
        cardsList = this.getCardsList(),
        song = this.getSong(),
        horizontalVideo = this.getHorizontalVideo(),
        verticalVideo = this.getVerticalVideo()
    )
}