package com.example.garden.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.garden.database.converters.Converters
import com.example.garden.database.converters.GridGenreConverters
import com.example.garden.database.converters.ImageDataConverter
import com.example.garden.database.converters.LinkDataConverter
import com.example.garden.database.converters.ObjectDataConverter
import com.example.garden.database.dao.EpisodeProgressDao
import com.example.garden.database.dao.MediaGroupDao
import com.example.garden.database.dao.ObjectDataDao

@Database(
    entities = [
        ObjectEntity::class,
        EpisodeProgressEntity::class,
        MediaGroupEntity::class,
    ],
    version = 13,
    exportSchema = false
)
@TypeConverters(
    Converters::class,
    ImageDataConverter::class,
    LinkDataConverter::class,
    ObjectDataConverter::class,
    GridGenreConverters::class,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun objectDataDao(): ObjectDataDao
    abstract fun episodeProgressDao(): EpisodeProgressDao
    abstract fun mediaGroupDao(): MediaGroupDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "garden_beta.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
