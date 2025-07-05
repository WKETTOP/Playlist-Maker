package com.example.playlistmaker.library.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.model.PlaylistUiModel
import com.example.playlistmaker.library.ui.model.PlaylistViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var _playlistViewState = MutableStateFlow<PlaylistViewState>(PlaylistViewState.Empty)
    val playlistViewState: StateFlow<PlaylistViewState> = _playlistViewState

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    _playlistViewState.value = PlaylistViewState.Empty
                } else {
                    val uiModels = playlists.map { playlist ->
                        mapPlaylistToUiModel(playlist)
                    }
                    _playlistViewState.value = PlaylistViewState.Content(uiModels)
                }
            }
        }
    }

    private fun mapPlaylistToUiModel(playlist: Playlist): PlaylistUiModel {
        val coverUri = playlist.coverImagePath?.let { path ->
            if (path.isNotEmpty()) {
                val file = File(path)
                if (file.exists()) {
                    Uri.fromFile(file)
                } else {
                    null
                }
            } else {
                null
            }
        }

        return PlaylistUiModel(
            playlistId = playlist.playlistId,
            title = playlist.title,
            description = playlist.description,
            coverUri = coverUri,
            trackCount = playlist.trackCount,
            createdAt = playlist.createdAt
        )
    }
}
