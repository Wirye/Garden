package com.example.garden.database.groups

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [groupsData::class], version = 3)
abstract class AppGroupsDataBase: RoomDatabase() {
    abstract fun groupsDataDao(): groupsDataDao
    companion object {
        @Volatile
        private var Instance: AppGroupsDataBase? = null
        fun getDatabase(context: Context): AppGroupsDataBase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppGroupsDataBase::class.java,
                    "garden_betaGr.db"
                ).build().also { Instance = it }
            }
        }
    }
}