package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.dao.PlaylistDao
import com.example.playlistmaker.library.data.dao.PlaylistTrackDao
import com.example.playlistmaker.library.domain.dp.PlaylistRepository
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistDao.getPlaylistById(playlistId)?.let {
            playlistDbConverter.map(it)
        }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ): Boolean {
        if (playlist.trackIds.contains(track.trackId)) {
            return false
        }

        playlistTrackDao.insertTrack(trackDbConverter.mapToPlaylistTrack(track))
        val updateTracksIds = playlist.trackIds + track.trackId
        val updatePlaylist = playlist.copy(
            trackIds = updateTracksIds,
            trackCount = playlist.trackCount + 1
        )
        playlistDao.updatePlaylist(playlistDbConverter.map(updatePlaylist))

        return true
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistDao.getAllPlaylist()
        emit(playlists.map { playlistDbConverter.map(it) })
    }
}