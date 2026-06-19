package com.example.garden.database.groups

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groupsData", indices = [androidx.room.Index(value = ["id", "position"])])
data class GroupsData(
    @PrimaryKey(autoGenerate = true)
    var grIDi: Int,
    var groupNumber: Int,
    var id: Long,
    var position: Int,
)