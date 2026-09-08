package com.example.garden.ui.utils

import com.example.garden.database.CarouselType
import com.example.garden.database.CollectionType

fun getAllCollectionByCarouselType(carouselType: CarouselType): List<CollectionType> {
    return CollectionType.entries.toList().filter { carouselType in (it.forCarouselType ?: CarouselType.entries.toList()) }
}