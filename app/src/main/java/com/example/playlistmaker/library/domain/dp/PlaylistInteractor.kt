package com.example.playlistmaker.library.domain.dp

import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlistId: Int)

    suspend fun getPlaylistById(playlistId: Int): Playlist?

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean

    fun getAllPlaylists(): Flow<List<Playlist>>
}