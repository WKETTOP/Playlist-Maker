package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.dao.TrackDao
import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.library.domain.dp.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConverter: TrackDbConverter
) : FavoriteTracksRepository {

    override suspend fun addTrackToFavorite(track: Track) {
        trackDao.insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFavorite(track: Track) {
        trackDao.deleteTrack(trackDbConverter.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = trackDao.getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getFavoriteTracksId(): Flow<List<String>> = flow {
        val tracksId = trackDao.getTracksId()
        emit(tracksId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}
