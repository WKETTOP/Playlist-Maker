package com.example.playlistmaker.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.FavoriteTracksInteractor
import com.example.playlistmaker.library.ui.model.FavoriteTracksViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _favoriteTracksViewState = MutableStateFlow<FavoriteTracksViewState>(FavoriteTracksViewState.Empty)
    val favoriteTracksViewState: StateFlow<FavoriteTracksViewState> = _favoriteTracksViewState.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteTracksInteractor.getFavoriteTracks().collectLatest { tracks ->
                _favoriteTracksViewState.value = if (tracks.isEmpty()) {
                    FavoriteTracksViewState.Empty
                } else {
                    FavoriteTracksViewState.Content(tracks)
                }
            }
        }
    }
}
