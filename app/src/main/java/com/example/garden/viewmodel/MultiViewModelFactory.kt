package com.example.garden.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.garden.appsettings.SettingsManager
import com.example.garden.database.groups.GroupsDataDao
import com.example.garden.database.ObjectDataDao

class MultiViewModelFactory(private val dao: ObjectDataDao, private val groupsDao: GroupsDataDao, private val settings: SettingsManager, private val baseDensity: Float) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(dao, groupsDao, settings, baseDensity) as T
            }

            else -> throw IllegalArgumentException("Неизвестный класс ViewModel: ${modelClass.name}")
        }
    }
}