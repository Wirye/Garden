package com.example.garden.database.groups

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupsDataDao {

    @Query("SELECT * FROM groupsData")
    fun getAll(): Flow<List<GroupsData>>

    @Query("SELECT * FROM groupsData WHERE id = :id ORDER BY position ASC")
    fun getById(id: Long): List<GroupsData>

    @Query("SELECT * FROM groupsData WHERE groupNumber = :groupNumber ORDER BY position ASC")
    fun getByGroupNumber(groupNumber: Int): List<GroupsData>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    fun insert(gropsData: GroupsData)
    @Query("DELETE FROM groupsData")
    fun deleteAll()
}