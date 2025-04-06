package com.example.playlistmaker.search.ui.model

import com.example.playlistmaker.search.domain.model.Track

data class SearchViewState(
    val searchState: SearchState = SearchState.Empty(""),
    val searchQuery: String = "",
    val uiEvent: UiEvents = UiEvents()
) {
    data class UiEvents(
        val navigateToTrack: Track? = null,
        val showToast: String? = null,
        val hideKeyboard: Boolean = false
    )
}
