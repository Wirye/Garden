package com.example.garden.repository.artists

import com.example.garden.database.dao.ObjectDataDao
import com.example.garden.database.entities.ArtistType
import com.example.garden.database.entities.ElementType
import com.example.garden.database.entities.ObjectData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtistsRepositoryImpl(private val dao: ObjectDataDao) : ArtistsRepository {
    override fun searchArtists(
        query: String,
        artistTypes: List<ArtistType>
    ): Flow<List<ObjectData.Card.Artist>> {
        return dao.globalSearch(
            query = query,
            allowedTypes = listOf(ElementType.ArtistCard.name)
        ).map { list ->
            list.mapNotNull { it.info as? ObjectData.Card.Artist }
                .filter { artist ->
                    artistTypes.isEmpty() || artist.artistType in artistTypes
                }
        }
    }
}
