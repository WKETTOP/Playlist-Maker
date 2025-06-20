package com.example.playlistmaker.library.domain.dp

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    suspend fun addTrackToFavorite(track: Track)

    suspend fun deleteTrackFromFavorite(track: Track)

    fun getTrackId(trackId: String): Flow<Boolean>

    fun getFavoriteTracks(): Flow<List<Track>>
}