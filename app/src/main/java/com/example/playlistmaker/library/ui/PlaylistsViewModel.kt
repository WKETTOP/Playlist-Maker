package com.example.playlistmaker.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.model.PlaylistViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var _playlistViewState = MutableStateFlow<PlaylistViewState>(PlaylistViewState.Empty)
    val playlistViewState: StateFlow<PlaylistViewState> = _playlistViewState

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            _playlistViewState.update { PlaylistViewState.Empty }
        } else {
            _playlistViewState.update { PlaylistViewState.Content(playlists) }
        }
    }
}
