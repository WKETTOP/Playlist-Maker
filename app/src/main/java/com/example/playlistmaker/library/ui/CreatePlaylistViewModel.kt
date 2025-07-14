package com.example.playlistmaker.library.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.model.CreatePlaylistViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    protected var _screenState = MutableStateFlow(CreatePlaylistViewState())
    val screenState: StateFlow<CreatePlaylistViewState> = _screenState

    private var _playlistCreated = MutableStateFlow<String?>(null)
    val playlistCreated: StateFlow<String?> = _playlistCreated

    fun updatePlaylistTitle(title: String) {
        _screenState.value = _screenState.value.copy(
            playlistTitle = title,
            isCreateButtonEnabled = title.isNotBlank()
        )
    }

    fun updatePlaylistDescription(description: String) {
        _screenState.value = _screenState.value.copy(playlistDescription = description)
    }

    fun saveCoverImage(uri: Uri) {
        viewModelScope.launch {
            val savedImagePath = playlistInteractor.savePlaylistCover(uri)
            if (savedImagePath != null) {
                updateCoverImage(savedImagePath)
            }
        }
    }

    private fun updateCoverImage(path: String) {
        _screenState.value = _screenState.value.copy(coverImageUri = path)
    }

    fun createPlaylist() {
        viewModelScope.launch {
            val state = _screenState.value
            if (state.playlistTitle.isNotBlank()) {
                val playlist = Playlist(
                    title = state.playlistTitle,
                    description = state.playlistDescription,
                    coverImagePath = state.coverImageUri.orEmpty(),
                    trackCount = 0
                )
                playlistInteractor.createPlaylist(playlist)
                _playlistCreated.value = playlist.title
            }
        }
    }

    open fun hasUnsavedChanges(): Boolean {
        val state = _screenState.value
        return state.playlistTitle.isNotBlank() || state.playlistDescription.isNotBlank() || state.coverImageUri != null
    }

    fun playlistCreateMessageShow() {
        _playlistCreated.value = null
    }
}