package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.garden.database.entities.RecentQueriesEntity
import com.example.garden.repository.recentQueries.RecentQueriesRepository
import kotlinx.coroutines.launch

class RecentQueriesViewModel(private val repository: RecentQueriesRepository) : ViewModel() {
    fun getRecentQueries() = repository.getRecentQueries()

    fun deleteRecentQuery(entity: RecentQueriesEntity) {
        viewModelScope.launch {
            repository.deleteRecentQuery(entity)
        }
    }

    fun addOrUpRecentQuery(entity: RecentQueriesEntity) {
        viewModelScope.launch {
            repository.addOrUpRecentQuery(entity)
        }
    }
}
