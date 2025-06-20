package com.example.playlistmaker.library.domain.dp

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(track: Track)

    fun getFavoriteTrackId(trackId: String): Flow<Boolean>

    fun getFavoriteTracks(): Flow<List<Track>>
}
