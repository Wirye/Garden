package com.example.garden.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.garden.database.ElementType
import com.example.garden.database.LinkData
import com.example.garden.database.ObjectEntity
import com.example.garden.database.ObjectWithChilds
import com.example.garden.database.PageType
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectDataDao {
    @Upsert
    suspend fun upsertObject(entity: ObjectEntity): Long

    @Upsert
    suspend fun upsertObjects(entities: List<ObjectEntity>)

    @Query("SELECT * FROM objectData WHERE page = :page AND parentId IS NULL ORDER BY position ASC")
    fun getRootObjectsForPage(page: PageType): Flow<List<ObjectEntity>>

    @Query("SELECT * FROM objectData WHERE parentId = :parentId ORDER BY position ASC")
    fun getChildObjects(parentId: Long): Flow<List<ObjectEntity>>

    @Transaction
    @Query("SELECT * FROM objectData WHERE page = :page AND parentId IS NULL ORDER BY position ASC")
    fun getRootObjectsWithChildren(page: PageType): PagingSource<Int, ObjectWithChilds>

    @Query("SELECT * FROM objectData WHERE parentId = :parentId ORDER BY position ASC")
    suspend fun getChildObjectsDirect(parentId: Long): List<ObjectEntity>

    @Query("SELECT * FROM objectData WHERE elementType = :elementType ORDER BY position ASC")
    fun getPagingObjectsByElementType(elementType: ElementType): PagingSource<Int, ObjectEntity>

    @Query("SELECT * FROM objectData WHERE page = :page AND parentId IS NULL ORDER BY position ASC")
    fun getPagingObjectsByPage(page: PageType): PagingSource<Int, ObjectEntity>

    @Query("SELECT * FROM objectData WHERE parentId = :parentId ORDER BY position ASC")
    fun getChildrenPagingByParentId(parentId: Long): PagingSource<Int, ObjectEntity>

    @Query("SELECT * FROM objectData WHERE id = :id LIMIT 1")
    suspend fun getObjectById(id: Long): ObjectEntity?

    @Query("SELECT * FROM objectData WHERE id = :id LIMIT 1")
    fun observeObjectById(id: Long): Flow<ObjectEntity?>

    @Query("SELECT * FROM objectData WHERE link = :link LIMIT 1")
    fun getObjectByLink(link: LinkData): Flow<ObjectEntity?>

    @Query("SELECT * FROM objectData WHERE parentId = :parentId ORDER BY position ASC")
    fun getCardsByParentId(parentId: Long) : Flow<List<ObjectEntity>>

    @Query("SELECT * FROM objectData WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ObjectEntity?

    @Query("UPDATE objectData SET position = :newPosition WHERE id = :id")
    suspend fun updatePosition(id: Long, newPosition: Int)

    @Query("""
        SELECT objectData.* FROM objectData
        JOIN objectData_fts ON objectData.id = objectData_fts.rowid
        WHERE objectData_fts MATCH :query
          AND objectData.elementType IN (:allowedTypes)
          AND objectData.link NOT LIKE 'insert%'
        ORDER BY objectData.position ASC
    """)
    fun searchCardsFts(
        query: String,
        allowedTypes: List<String>
    ): PagingSource<Int, ObjectEntity>

    @Query("""
        SELECT * FROM objectData
        WHERE elementType IN (:allowedTypes)
          AND link NOT LIKE 'insert%'
        ORDER BY position ASC
    """)
    fun getAllCardsPaging(
        allowedTypes: List<String>
    ): PagingSource<Int, ObjectEntity>

    @Query("""
    DELETE FROM objectData 
    WHERE id = :id 
       OR parentId = :id 
       OR link = 'insert:' || :id
""")
    suspend fun deleteById(id: Long)

    @Query("""
        UPDATE objectData 
        SET position = position - 1 
        WHERE parentId IS :parentId
          AND position > :deletedPosition
    """)
    suspend fun shiftPositionsUp(parentId: Long?, deletedPosition: Int)

    @Transaction
    suspend fun deleteObject(
        id: Long,
        parentId: Long?,
        deletedPosition: Int
    ) {
        deleteById(id)
        shiftPositionsUp(parentId, deletedPosition)
    }

    @Query("DELETE FROM objectData WHERE page = :page")
    suspend fun clearPage(page: PageType)

    @Query("DELETE FROM objectData")
    suspend fun clearAll()

    @Query("SELECT MAX(position) FROM objectData WHERE parentId IS :parentId")
    suspend fun getMaxChildPosition(parentId: Long?) : Int?
}
