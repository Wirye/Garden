package com.example.garden

import android.app.Application
import com.example.garden.appsettings.SettingsManager
import com.example.garden.database.AppDatabase
import com.example.garden.database.groups.AppGroupsDataBase
import com.example.garden.database.groups.GroupsDataDao
import com.example.garden.database.ObjectDataDao

class App: Application() {
    lateinit var settingsManager: SettingsManager
    lateinit var database: AppDatabase
    lateinit var dao: ObjectDataDao
    lateinit var groupsDataBase: AppGroupsDataBase
    lateinit var groupsDao: GroupsDataDao

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        database = AppDatabase.getDatabase(this)
        dao = database.objectDataDao()
        groupsDataBase = AppGroupsDataBase.getDatabase(this)
        groupsDao = groupsDataBase.groupsDataDao()
    }
}