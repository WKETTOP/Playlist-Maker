package com.example.playlistmaker.search.ui.model

import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(val tracks: List<Track>) : SearchState

    data class History(val tracks: List<Track>) : SearchState

    data class Error(val errorMessage: String) : SearchState

    data class Empty(val message: String) : SearchState
}
