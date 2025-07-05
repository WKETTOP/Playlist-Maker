package com.example.playlistmaker.library.domain.dp

import android.net.Uri
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean

    suspend fun removeTrackFromPlaylist(track: Track, playlist: Playlist)

    suspend fun getTracksFromPlaylist(playlist: Playlist): List<Track>

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun savePlaylistCover(uri: Uri): String?
}