package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.FavoriteTrackDbConverter
import com.example.playlistmaker.library.data.dao.FavoriteTrackDao
import com.example.playlistmaker.library.data.db.FavoriteTrackEntity
import com.example.playlistmaker.library.domain.dp.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao,
    private val favoriteTrackDbConverter: FavoriteTrackDbConverter
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        favoriteTrackDao.insertTrack(favoriteTrackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        favoriteTrackDao.deleteTrack(favoriteTrackDbConverter.map(track))
    }

    override fun getFavoriteTrackId(trackId: String) = flow {
        val id = favoriteTrackDao.getTrackId(trackId)
        emit(id)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = favoriteTrackDao.getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { track -> favoriteTrackDbConverter.map(track) }
    }
}
