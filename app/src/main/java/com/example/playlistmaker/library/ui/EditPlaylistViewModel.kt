package com.example.playlistmaker.library.ui

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.model.OnePlaylistUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : CreatePlaylistViewModel(playlistInteractor) {

    private var _playlistEdited = MutableStateFlow<String?>(null)
    val playlistEdited: StateFlow<String?> = _playlistEdited

    private var editingPlaylistId: Int = 0
    private var originalPlaylist: OnePlaylistUiModel? = null

    fun initializeForEdit(playlist: OnePlaylistUiModel) {
        originalPlaylist = playlist
        editingPlaylistId = playlist.playlistId

        updatePlaylistTitle(playlist.title)
        updatePlaylistDescription(playlist.description)

        playlist.coverUri?.let { uri ->
            if (!uri.path.isNullOrEmpty()) {
                _screenState.value = _screenState.value.copy(coverImageUri = uri.path)
            }
        }
    }

    fun updatePlaylist() {
        viewModelScope.launch {
            if (_screenState.value.playlistTitle.isNotBlank()) {
                val currentPlaylist = playlistInteractor.getPlaylistWithTracks(editingPlaylistId)
                val updatedPlaylist = Playlist(
                    playlistId = editingPlaylistId,
                    title = _screenState.value.playlistTitle,
                    description = _screenState.value.playlistDescription,
                    coverImagePath = _screenState.value.coverImageUri.orEmpty(),
                    trackCount = currentPlaylist?.trackCount ?: 0,
                    createdAt = currentPlaylist?.createdAt ?: System.currentTimeMillis()
                )
                playlistInteractor.updatePlaylist(updatedPlaylist)
                _playlistEdited.value = updatedPlaylist.title
            }
        }
    }

    fun playlistUpdateMessageShow() {
        _playlistEdited.value = null
    }

    override fun hasUnsavedChanges(): Boolean {
        val state = _screenState.value
        val original = originalPlaylist ?: return super.hasUnsavedChanges()

        return state.playlistTitle != original.title ||
                state.playlistDescription != original.description ||
                hasImageChanged()
    }

    private fun hasImageChanged(): Boolean {
        val state = _screenState.value
        val original = originalPlaylist ?: return  false

        val currentImagePath = state.coverImageUri
        val originalImagePath = original.coverUri?.path

        return currentImagePath != originalImagePath
    }
}
