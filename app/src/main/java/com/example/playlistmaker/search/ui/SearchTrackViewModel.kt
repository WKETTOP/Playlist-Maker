package com.example.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.model.SearchViewState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchTrackViewModel(
    private val tracksInteractor: TracksInteractor,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val EMPTY_QUERY_HISTORY_DELAY = 100L
    }

    private val _state = MutableStateFlow(SearchViewState())
    val state: StateFlow<SearchViewState> = _state.asStateFlow()

    private var lastSearchQuery: String? = null
    private var isTrackClickAllowed = true
    private var searchJob: Job? = null

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            performSearch(changedText)
        }

    private val showHistoryDebounce =
        debounce<Unit>(EMPTY_QUERY_HISTORY_DELAY, viewModelScope, true) {
            showHistory()
        }

    private val clickDebounceReset =
        debounce<Unit>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isTrackClickAllowed = true
        }

    init {
        loadHistory()
    }

    fun onQueryChanged(query: String) {

        _state.update { currentState ->
            currentState.copy(searchQuery = query)
        }

        if (query.isEmpty()) {
            searchJob?.cancel()
            showHistoryDebounce(Unit)
        } else {
            searchDebounce(query)
        }
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchQuery != changedText) {
            lastSearchQuery = changedText
            trackSearchDebounce(changedText)
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            return
        }

        searchJob?.cancel()

        _state.update { currentState ->
            currentState.copy(
                searchQuery = query,
                searchState = SearchState.Loading,
                uiEvent = SearchViewState.UiEvents()
            )
        }

        searchJob = viewModelScope.launch {
            tracksInteractor.searchTrack(query)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }


    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        _state.update { currentState ->
            when {
                errorMessage != null -> {
                    currentState.copy(
                        searchState = SearchState.Error(errorMessage),
                        uiEvent = SearchViewState.UiEvents(showToast = errorMessage)
                    )
                }

                foundTracks.isNullOrEmpty() -> {
                    currentState.copy(
                        searchState = SearchState.Empty("")
                    )
                }

                else -> {
                    currentState.copy(
                        searchState = SearchState.Content(foundTracks)
                    )
                }
            }
        }
    }

    private fun trackClickDebounce(): Boolean {
        val current = isTrackClickAllowed

        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            clickDebounceReset(Unit)
        }

        return current
    }

    private fun showHistory() {
        viewModelScope.launch {
            val historyTracks = tracksInteractor.getTrackSearchHistory()
            _state.update { currentState ->
                currentState.copy(searchState = SearchState.History(historyTracks))
            }

        }

    }

        private fun triggerHideKeyboard() {
            _state.update { currentState ->
                currentState.copy(
                    uiEvent = _state.value.uiEvent.copy(hideKeyboard = true)
                )
            }
        }

        fun onTrackClicked(track: Track) {
            if (!trackClickDebounce()) return

            viewModelScope.launch {
                tracksInteractor.saveTrack(track)
                _state.update { currentState ->
                    currentState.copy(
                        uiEvent = _state.value.uiEvent.copy(navigateToTrack = track)
                    )
                }
            }

        }

        fun clearSearch() {
            viewModelScope.launch {
                _state.update { currentState ->
                    currentState.copy(
                        searchQuery = "",
                        searchState = SearchState.History(tracksInteractor.getTrackSearchHistory())
                    )
                }
            }

            lastSearchQuery = ""
            triggerHideKeyboard()
        }

        fun clearHistory() {
            tracksInteractor.clearTrackSearchHistory()
            showHistory()
        }

        fun loadHistory() {
            if (_state.value.searchQuery.isEmpty()) {
                showHistory()
            }
        }

        fun clearEvents() {
            _state.update { currentState ->
                currentState.copy(uiEvent = SearchViewState.UiEvents())
            }
        }
    }
