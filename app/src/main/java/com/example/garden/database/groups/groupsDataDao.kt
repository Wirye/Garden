package com.example.garden.database.groups

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface groupsDataDao {

    @Query("SELECT * FROM groupsData")
    fun getAll(): Flow<List<groupsData>>

    @Query("SELECT * FROM groupsData WHERE id = :id ORDER BY position ASC")
    fun getById(id: Long): List<groupsData>

    @Query("SELECT * FROM groupsData WHERE groupNumber = :groupNumber ORDER BY position ASC")
    fun getByGroupNumber(groupNumber: Int): List<groupsData>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun insert(gropsData: groupsData)
    @Query("DELETE FROM groupsData")
    fun deleteAll()
}