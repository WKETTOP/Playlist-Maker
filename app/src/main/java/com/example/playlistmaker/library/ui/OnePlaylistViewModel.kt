package com.example.playlistmaker.library.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.ui.model.OnePlaylistUiModel
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnePlaylistViewModel(
    private val application: Application,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistState = MutableStateFlow<OnePlaylistUiModel?>(null)
    val playlistState: StateFlow<OnePlaylistUiModel?> = _playlistState

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistWithTracks(playlistId)
            if (playlist != null) {
                val tracks = playlistInteractor.getTracksFromPlaylist(playlist)
                val totalDurationMs = tracks.sumOf { it.trackTimeMillis }
                val totalDuration = formatDuration(totalDurationMs)

                val coverUri = playlistInteractor.getPlaylistCoverUri(playlist.coverImagePath)

                _playlistState.value = OnePlaylistUiModel(
                    playlistId = playlist.playlistId,
                    title = playlist.title,
                    description = playlist.description ?: "",
                    coverUri = coverUri,
                    trackCount = formatTrackCount(tracks.size),
                    totalDuration = totalDuration,
                    tracks = tracks,
                    createAt = playlist.createdAt,
                )
            }
        }
    }

    fun removeTrackFromPlaylist(track: Track, playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistWithTracks(playlistId)
            if (playlist != null) {
                playlistInteractor.removeTrackFromPlaylist(track, playlist)
                loadPlaylist(playlistId)
            }
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistWithTracks(playlistId)?.let {
                playlistInteractor.deletePlaylist(it)
            }
        }
    }

    private fun formatDuration(duration: Long): String {
        val totalSeconds = duration / 1000
        val minutes = totalSeconds / 60
        return if (minutes > 0) {
            application.resources.getQuantityString(
                R.plurals.duration_minutes,
                minutes.toInt(),
                minutes.toInt()
            )
        } else {
            application.resources.getString(R.string.less_minute)
        }

    }

    private fun formatTrackCount(count: Int): String {
        return application.resources.getQuantityString(
            R.plurals.track_count,
            count,
            count
        )
    }
}