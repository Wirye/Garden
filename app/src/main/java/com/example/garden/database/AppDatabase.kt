package com.example.garden.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.garden.database.converters.Converters
import com.example.garden.database.converters.GridGenreConverters
import com.example.garden.database.converters.ImageDataConverter
import com.example.garden.database.converters.LinkDataConverter
import com.example.garden.database.converters.ObjectDataConverter
import com.example.garden.database.dao.EpisodeProgressDao
import com.example.garden.database.dao.MediaGroupDao
import com.example.garden.database.dao.ObjectDataDao
import com.example.garden.database.dao.RecentQueriesDao
import com.example.garden.database.dao.WebObjectDataDao
import com.example.garden.database.entities.EpisodeProgressEntity
import com.example.garden.database.entities.MediaGroupEntity
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.database.entities.ObjectFtsEntity
import com.example.garden.database.entities.RecentQueriesEntity
import com.example.garden.database.entities.ResolvedObjectEntity
import com.example.garden.database.entities.WebObjectEntity

@Database(
    entities = [
        ObjectEntity::class,
        EpisodeProgressEntity::class,
        MediaGroupEntity::class,
        ObjectFtsEntity::class,
        RecentQueriesEntity::class,
        WebObjectEntity::class,
    ],
    views = [ResolvedObjectEntity::class],
    version = 28,
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
    abstract fun resentQueriesDao(): RecentQueriesDao
    abstract fun webObjectDataDao(): WebObjectDataDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                try {
                    buildDatabase(context)
                } catch (_: Exception) {
                    context.deleteDatabase("garden_beta.db")
                    context.deleteDatabase("garden_beta_v14.db")
                    buildDatabase(context)
                }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "garden_beta.db"
            )
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .fallbackToDestructiveMigration(true)
                .build()
                .also { Instance = it }
        }
    }
}
