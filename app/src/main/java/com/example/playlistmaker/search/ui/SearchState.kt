package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(val tracks: MutableList<Track>) : SearchState

    data class Error(val errorMessage: String) : SearchState

    data class Empty(val message: String) : SearchState
}
