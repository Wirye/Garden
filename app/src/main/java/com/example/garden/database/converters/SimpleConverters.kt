package com.example.garden.database.converters

import androidx.room.TypeConverter
import com.example.garden.database.CardSize
import com.example.garden.database.CarouselType
import com.example.garden.database.ElementType
import com.example.garden.database.LayoutType
import com.example.garden.database.PageType
import com.example.garden.database.SizeType

class Converters {
    @TypeConverter
    fun fromCornerType(value: SizeType): String = value.name
    @TypeConverter
    fun toCornerType(value: String): SizeType = SizeType.valueOf(value)

    @TypeConverter
    fun fromCardSize(value: CardSize): String = value.name
    @TypeConverter
    fun toCardSize(value: String): CardSize = CardSize.valueOf(value)

    @TypeConverter
    fun fromElementType(value: ElementType): String = value.name
    @TypeConverter
    fun toElementType(value: String): ElementType = ElementType.valueOf(value)

    @TypeConverter
    fun fromCarouselType(value: CarouselType): String = value.name
    @TypeConverter
    fun toCarouselType(value: String): CarouselType = CarouselType.valueOf(value)

    @TypeConverter
    fun fromPageType(value: PageType): String = value.name
    @TypeConverter
    fun toPageType(value: String): PageType = PageType.valueOf(value)

    @TypeConverter
    fun fromLayoutType(value: LayoutType): String = value.name
    @TypeConverter
    fun toLayoutType(value: String): LayoutType = LayoutType.valueOf(value)
}