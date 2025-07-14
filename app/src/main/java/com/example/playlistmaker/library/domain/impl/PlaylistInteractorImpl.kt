package com.example.playlistmaker.library.domain.impl

import android.net.Uri
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.domain.dp.PlaylistRepository
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistRepository.createPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ): Boolean {
        return playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun removeTrackFromPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        return playlistRepository.removeTrackFromPlaylist(track.trackId, playlist.playlistId)
    }

    override suspend fun getTracksFromPlaylist(playlist: Playlist): List<Track> {
        return playlistRepository.getTracksFromPlaylist(playlist.playlistId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        return playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        return playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun savePlaylistCover(uri: Uri): String? {
         return playlistRepository.savePlaylistCover(uri)
    }

    override suspend fun getPlaylistWithTracks(playlistId: Int): Playlist? {
        return playlistRepository.getPlaylistWithTracks(playlistId)
    }

    override suspend fun getPlaylistCoverUri(coverImagePath: String?): Uri? {
        return playlistRepository.getPlaylistCoverUri(coverImagePath)
    }
}