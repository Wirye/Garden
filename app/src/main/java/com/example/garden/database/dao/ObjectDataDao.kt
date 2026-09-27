package com.example.garden.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.ObjectEntity
import com.example.garden.database.ObjectWithChilds
import com.example.garden.database.entities.PageType
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectDataDao {
    @Upsert
    suspend fun justUpsertObject(entity: ObjectEntity): Long

    @Query(
        """
        UPDATE objectData SET position = position + 1 WHERE parentId IS :parentId AND position >= :newCardPosition
    """
    )
    suspend fun updatePositions(parentId: Long?, newCardPosition: Int)

    @Query("""
        DELETE FROM objectData 
        WHERE elementType != 'ArtistCard'
          AND (
            json_extract(info, '$.author') = :artistName COLLATE NOCASE
            OR 
            json_extract(info, '$.authors') LIKE '%"' || :artistName || '"%' COLLATE NOCASE
          )
    """)
    suspend fun deleteLinkedCards(artistName: String): Int

    @Transaction
    suspend fun upsertObject(entity: ObjectEntity): Long {
        updatePositions(parentId = entity.parentId, newCardPosition = entity.position)
        return justUpsertObject(entity)
    }

    @Transaction
    @Query("SELECT * FROM objectData WHERE page = :page AND parentId IS NULL ORDER BY position ASC")
    fun getRootObjectsWithChildren(page: PageType): PagingSource<Int, ObjectWithChilds>

    @Query("SELECT * FROM objectData WHERE parentId IS 1 ORDER BY position ASC")
    fun getUnspecifiedPageObjects(): PagingSource<Int, ObjectEntity>

    @Query("SELECT id FROM objectData WHERE LOWER(name) IS LOWER(:name) AND elementType IS :type AND link NOT LIKE 'insert%' LIMIT 1")
    suspend fun isAuthorExist(name: String, type: ElementType = ElementType.ArtistCard): Long?

    @Query(
        """
    SELECT 
        child.id AS id,
        child.page AS page,
        child.parentId AS parentId,
        child.position AS position,
        child.elementType AS elementType,
        child.link AS link,
        child.isUserCreated AS isUserCreated,
        child.source AS source,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.name 
            ELSE child.name 
        END AS name,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.info 
            ELSE child.info 
        END AS info

    FROM objectData AS child
    LEFT JOIN objectData AS parent 
        ON child.link LIKE 'insert:%' 
       AND CAST(SUBSTR(child.link, 8) AS INTEGER) = parent.id
       
    WHERE child.elementType = :elementType
    ORDER BY child.position ASC
    """
    )
    fun getPagingObjectsByElementType(elementType: ElementType): PagingSource<Int, ObjectEntity>

    @Query(
        """
    SELECT 
        child.id AS id,
        child.page AS page,
        child.parentId AS parentId,
        child.position AS position,
        child.elementType AS elementType,
        child.link AS link,
        child.isUserCreated AS isUserCreated,
        child.source AS source,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.name 
            ELSE child.name 
        END AS name,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.info 
            ELSE child.info 
        END AS info

    FROM objectData AS child
    LEFT JOIN objectData AS parent 
        ON child.link LIKE 'insert:%' 
       AND CAST(SUBSTR(child.link, 8) AS INTEGER) = parent.id
    
    WHERE child.page = :page AND child.parentId IS NULL 
    ORDER BY child.position ASC
"""
    )
    fun getPagingObjectsByPage(page: PageType): PagingSource<Int, ObjectEntity>

    @Query(
        """
    SELECT 
        child.id AS id,
        child.page AS page,
        child.parentId AS parentId,
        child.position AS position,
        child.elementType AS elementType,
        child.link AS link,
        child.isUserCreated AS isUserCreated,
        child.source AS source,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.name 
            ELSE child.name 
        END AS name,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.info 
            ELSE child.info 
        END AS info

    FROM objectData AS child
    LEFT JOIN objectData AS parent 
        ON child.link LIKE 'insert:%' 
       AND CAST(SUBSTR(child.link, 8) AS INTEGER) = parent.id
            
    WHERE child.parentId = :parentId 
    ORDER BY child.position ASC
"""
    )
    fun getChildrenPagingByParentId(parentId: Long): PagingSource<Int, ObjectEntity>

    @Query(
        """
        SELECT 
        child.id AS id,
        child.page AS page,
        child.parentId AS parentId,
        child.position AS position,
        child.elementType AS elementType,
        child.link AS link,
        child.isUserCreated AS isUserCreated,
        child.source AS source,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.name 
            ELSE child.name 
        END AS name,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.info 
            ELSE child.info 
        END AS info

    FROM objectData AS child
    LEFT JOIN objectData AS parent 
        ON child.link LIKE 'insert:%' 
        AND CAST(SUBSTR(child.link, 8) AS INTEGER) = parent.id
    WHERE child.id = :id
    LIMIT 1
    """
    )
    suspend fun getObjectById(id: Long): ObjectEntity?

    @Query(
        """
    SELECT 
        child.id AS id,
        child.page AS page,
        child.parentId AS parentId,
        child.position AS position,
        child.elementType AS elementType,
        child.link AS link,
        child.isUserCreated AS isUserCreated,
        child.source AS source,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.name 
            ELSE child.name 
        END AS name,
        
        CASE 
            WHEN child.link LIKE 'insert:%' AND parent.id IS NOT NULL 
            THEN parent.info 
            ELSE child.info 
        END AS info

    FROM objectData AS child
    LEFT JOIN objectData AS parent 
        ON child.link LIKE 'insert:%' 
       AND CAST(SUBSTR(child.link, 8) AS INTEGER) = parent.id
       
    WHERE child.parentId = :parentId
    ORDER BY child.position ASC
    """
    )
    fun getCardsByParentId(parentId: Long): Flow<List<ObjectEntity>>

    @Query("UPDATE objectData SET position = :newPosition WHERE id = :id")
    suspend fun updatePosition(id: Long, newPosition: Int)

    @Query(
        """
        SELECT objectData.* FROM objectData
        JOIN objectData_fts ON objectData.id = objectData_fts.rowid
        WHERE objectData_fts MATCH :query
          AND objectData.elementType IN (:allowedTypes)
          AND objectData.link NOT LIKE 'insert%'
    """
    )
    fun searchCardsFts(
        query: String,
        allowedTypes: List<String>
    ): PagingSource<Int, ObjectEntity>

    @Query(
        """
        SELECT objectData.* FROM objectData
        JOIN objectData_fts ON objectData.id = objectData_fts.rowid
        WHERE objectData_fts MATCH :query
            AND objectData.link NOT LIKE 'insert%'
            AND objectData.elementType IN (:allowedTypes)
        LIMIT 50
    """
    )
    fun globalSearch(
        query: String, allowedTypes: List<String> = listOf(
            ElementType.AnimeCard.name,
            ElementType.MangaCard.name,
            ElementType.MusicCard.name,
            ElementType.PlaylistCard.name,
            ElementType.AlbumCard.name,
            ElementType.ArtistCard.name
        )
    ): Flow<List<ObjectEntity>>

    @Query(
        """
        SELECT * FROM objectData
        WHERE elementType IN (:allowedTypes)
          AND link NOT LIKE 'insert%'
        ORDER BY position ASC
    """
    )
    fun getAllCardsPaging(
        allowedTypes: List<String>
    ): PagingSource<Int, ObjectEntity>

    @Query(
        """
    DELETE FROM objectData 
    WHERE id = :id 
       OR parentId = :id 
       OR link = 'insert:' || :id
"""
    )
    suspend fun deleteById(id: Long)

    @Query(
        """
        UPDATE objectData 
        SET position = position - 1 
        WHERE parentId IS :parentId
          AND position > :deletedPosition
    """
    )
    suspend fun shiftPositionsUp(parentId: Long?, deletedPosition: Int)

    @Transaction
    suspend fun deleteObject(
        id: Long,
        parentId: Long?,
        deletedPosition: Int,
        author: String? = null
    ) {
        if (author != null) {
            deleteLinkedCards(author.lowercase().trim())
        }
        deleteById(id)
        shiftPositionsUp(parentId, deletedPosition)
    }

    @Query("SELECT MAX(position) FROM objectData WHERE parentId IS :parentId AND isUserCreated IS true")
    suspend fun getMaxChildPosition(parentId: Long?): Int?
}
