package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.repository.objectLinks.ObjectLinksRepository
import kotlinx.coroutines.flow.Flow

class ObjectLinksViewModel(private val repository: ObjectLinksRepository) : ViewModel() {
    fun getRelatedObjects(id: Long): Flow<List<ObjectEntity>> = repository.getRelatedObjects(id)
}
