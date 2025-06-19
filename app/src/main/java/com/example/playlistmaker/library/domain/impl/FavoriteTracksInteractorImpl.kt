package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.dp.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.dp.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override suspend fun addTrackToFavorite(track: Track) {
        favoriteTracksRepository.addTrackToFavorite(track)
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        favoriteTracksRepository.deleteTrackFromFavorite(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }

    override fun getFavoriteTracksId(): Flow<List<String>> {
        return favoriteTracksRepository.getFavoriteTracksId()
    }
}
