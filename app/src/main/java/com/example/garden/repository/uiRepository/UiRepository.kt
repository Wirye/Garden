package com.example.garden.repository.uiRepository

import androidx.paging.PagingData
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.entities.PageType
import kotlinx.coroutines.flow.Flow

interface UiRepository {
    fun getPagePagingObjects(page: PageType): Flow<PagingData<ObjectWithChilds2>>
}