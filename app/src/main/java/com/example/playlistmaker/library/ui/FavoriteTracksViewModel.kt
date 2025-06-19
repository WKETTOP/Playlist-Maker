package com.example.playlistmaker.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.FavoriteTracksInteractor
import com.example.playlistmaker.library.ui.model.FavoriteTracksViewState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _favoriteTracksViewState =
        MutableStateFlow<FavoriteTracksViewState>(FavoriteTracksViewState.Empty)
    val favoriteTracksViewState: StateFlow<FavoriteTracksViewState> =
        _favoriteTracksViewState.asStateFlow()

    fun fillData() {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _favoriteTracksViewState.update { FavoriteTracksViewState.Empty }
        } else {
            _favoriteTracksViewState.update { FavoriteTracksViewState.Content(tracks) }
        }
    }
}
