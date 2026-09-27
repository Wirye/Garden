package com.example.garden.repository.artists

import com.example.garden.database.entities.ArtistType
import com.example.garden.database.entities.ObjectData
import kotlinx.coroutines.flow.Flow

interface ArtistsRepository {
    fun searchArtists(query: String, artistTypes: List<ArtistType>): Flow<List<ObjectData.Card.Artist>>
}
