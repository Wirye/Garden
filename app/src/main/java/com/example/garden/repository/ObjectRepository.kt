package com.example.garden.repository

import androidx.paging.PagingData
import com.example.garden.database.ElementType
import com.example.garden.database.ObjectData
import com.example.garden.database.ObjectEntity
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.PageType
import kotlinx.coroutines.flow.Flow

interface ObjectRepository {
    fun getPagePaging(page: PageType): Flow<PagingData<ObjectData.Carousel>>
    fun getPagePagingObjects(page: PageType): Flow<PagingData<ObjectWithChilds2>>
    fun getCarouselChildrenPaging(parentId: Long): Flow<PagingData<ObjectData.Card>>
    fun getCardsPaging(elementType: ElementType): Flow<PagingData<ObjectData.Card>>
    suspend fun saveObject(data: ObjectData, page: PageType, parentId: Long? = null): Long
    suspend fun saveCarouselWithChilds(carousel: ObjectData.Carousel, page: PageType): Long
    suspend fun getRootObjectById(id: Long): ObjectData?
    suspend fun getMaxChildPosition(parentId: Long?) : Int?
    suspend fun deleteObject(id: Long, parentId: Long?, position: Int)
    suspend fun getById(id: Long) : ObjectEntity?
}
