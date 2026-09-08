package com.example.garden.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters

@Database(entities = [ObjectData::class], version = 12)
@TypeConverters(Converters::class, ImageDataConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun objectDataDao(): ObjectDataDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "garden_beta.db"
                ).fallbackToDestructiveMigration(true).build().also { Instance = it }
            }
        }
    }
}