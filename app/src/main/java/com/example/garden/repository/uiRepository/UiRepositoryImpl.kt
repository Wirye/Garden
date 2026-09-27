package com.example.garden.repository.uiRepository

import androidx.paging.PagingData
import androidx.paging.map
import com.example.garden.database.ObjectWithChilds2
import com.example.garden.database.entities.CollectionType
import com.example.garden.database.entities.ObjectData
import com.example.garden.database.entities.PageType
import com.example.garden.repository.objects.ObjectRepository
import com.example.garden.repository.webObjects.WebObjectsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class UiRepositoryImpl(
    private val objectRepository: ObjectRepository,
    private val webObjectsRepository: WebObjectsRepository
) : UiRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPagePagingObjects(page: PageType): Flow<PagingData<ObjectWithChilds2>> {
        return webObjectsRepository.getAllChildsWithCollectionType()
            .flatMapLatest { webObjects ->
                objectRepository.getPagePagingObjects(page)
                    .map { pagingData ->
                        pagingData.map { carousel ->
                            val collectionType = carousel.parent.carouselCollectionType

                            if (collectionType != CollectionType.None) {
                                val startWebObjectPosition = carousel.childs.size

                                val webObjectsForCarousel = webObjects[collectionType].orEmpty()
                                    .mapIndexed { index, entity ->
                                        entity.info.injectWebObjectEntityData(entity)
                                            .copyWithPosition(index + startWebObjectPosition)
                                    }
                                    .filterIsInstance<ObjectData.Card>()

                                carousel.copy(
                                    childs = carousel.childs + webObjectsForCarousel
                                )
                            } else {
                                carousel
                            }
                        }
                    }
            }
    }
}
