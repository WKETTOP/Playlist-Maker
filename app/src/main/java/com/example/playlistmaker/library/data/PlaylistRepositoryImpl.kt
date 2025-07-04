package com.example.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.data.dao.PlaylistDao
import com.example.playlistmaker.library.data.dao.TrackDao
import com.example.playlistmaker.library.data.db.PlaylistTrackCrossRef
import com.example.playlistmaker.library.domain.dp.PlaylistRepository
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackDao: TrackDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val context: Context
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDao.insertPlaylist(playlistEntity)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        playlistDao.getAllPlaylists().collect { playlistEntities ->
            val playlistWithCount = playlistEntities.map { entity ->
                val playlist = playlistDbConverter.map(entity)
                val trackCount = playlistDao.getTrackCount(entity.playlistId)
                playlist.copy(trackCount = trackCount)
            }
            emit(playlistWithCount)
        }
    }

    override fun getAllPlaylistsWithTracks(): Flow<List<Playlist>> = flow {
        playlistDao.getAllPlaylistWithTracks().collect { playlistsWithTracks ->
            val playlistWithCount = playlistsWithTracks.map { playlistWithTracks ->
                playlistDbConverter.map(playlistWithTracks)
            }
            emit(playlistWithCount)
        }
    }

    override suspend fun getPlaylistWithTracks(playlistId: Int): Playlist? {
        val playlistWithTracks = playlistDao.getPlaylistWithTracks(playlistId)
        return playlistWithTracks?.let { playlistDbConverter.map(it) }
    }


    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        val isAlreadyInPlaylist = playlistDao.isTrackInPlaylist(playlist.playlistId, track.trackId)

        if (isAlreadyInPlaylist) {
            return false
        }

        val trackEntity = trackDbConverter.map(track)
        trackDao.insertTrack(trackEntity)

        val playlistTrackCrossRef = PlaylistTrackCrossRef(
            playlistId = playlist.playlistId,
            trackId = track.trackId,
            addedAt = System.currentTimeMillis()
        )
        playlistDao.insertPlaylistTrack(playlistTrackCrossRef)

        return true
    }

    override suspend fun removeTrackFromPlaylist(trackId: String, playlistId: Int) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    override suspend fun isTrackInPlaylist(trackId: String, playlistId: Int): Boolean {
        return playlistDao.isTrackInPlaylist(playlistId, trackId)
    }

    override suspend fun getTracksFromPlaylist(playlistId: Int): List<Track> {
        val playlistWithTracks = playlistDao.getPlaylistWithTracks(playlistId)
        return playlistWithTracks?.tracks?.map { trackEntity ->
            trackDbConverter.map(trackEntity)
        } ?: emptyList()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDao.deletePlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDao.insertPlaylist(playlistEntity)
    }

    override suspend fun savePlaylistCover(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val filePath = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "playlistmaker"
                )
                if (!filePath.exists()) {
                    filePath.mkdir()
                }

                val file = File(filePath, "cover.jpg")
                val inputStream = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                BitmapFactory
                    .decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                file.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }
}
