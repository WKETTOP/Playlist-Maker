package com.example.playlistmaker.library.domain.dp

import android.net.Uri
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun getAllPlaylistsWithTracks(): Flow<List<Playlist>>

    suspend fun getPlaylistWithTracks(playlistId: Int): Playlist?

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean

    suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int)

    suspend fun isTrackInPlaylist(trackId: String, playlistId: Int): Boolean

    suspend fun getTracksFromPlaylist(playlistId: Int): List<Track>

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun savePlaylistCover(uri: Uri): String?

    suspend fun getPlaylistCoverUri(coverImagePath: String?): Uri?
}
