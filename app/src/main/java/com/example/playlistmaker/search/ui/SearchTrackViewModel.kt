package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.model.SearchViewState
import com.example.playlistmaker.util.Creator

class SearchTrackViewModel(
    private val tracksInteractor: TracksInteractor,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchTrackViewModel(Creator.provideTracksInteractor())
            }
        }
    }

    private var handler = Handler(Looper.getMainLooper())

    private val _state = MutableLiveData(SearchViewState())
    val state: LiveData<SearchViewState> = _state

    private var lastSearchQuery: String? = null
    private var isTrackClickAllowed = true

    init {
        loadHistory()
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun onQueryChanged(query: String) {
        val currentState = _state.value ?: return

        _state.postValue(currentState.copy(searchQuery = query))

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        if (query.isEmpty()) {
            handler.postDelayed({ showHistory() }, 100)
        } else {
            searchDebounce(query)
        }
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchQuery == changedText) {
            return
        }
        this.lastSearchQuery = changedText

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { performSearch(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime
        )
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            return
        }
        _state.postValue(
            SearchViewState(
                searchQuery = query,
                searchState = SearchState.Loading,
                uiEvent = SearchViewState.UiEvents()
            )
        )

        tracksInteractor.searchTrack(
            query,
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val newState = when {
                        errorMessage != null -> {
                            _state.value?.copy(
                                searchState = SearchState.Error(errorMessage),
                                uiEvent = SearchViewState.UiEvents(showToast = errorMessage)
                            )
                        }

                        foundTracks.isNullOrEmpty() -> {
                            _state.value?.copy(
                                searchState = SearchState.Empty("")
                            )
                        }

                        else -> {
                            _state.value?.copy(
                                searchState = SearchState.Content(foundTracks)
                            )
                        }
                    }
                    newState?.let {
                        _state.postValue(it)
                    }
                }
            }
        )
    }

    private fun trackClickDebounce(): Boolean {
        val current = isTrackClickAllowed
        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            handler.postDelayed({ isTrackClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showHistory() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val historyTracks = tracksInteractor.getTrackSearchHistory()
        _state.postValue(
            (_state.value ?: SearchViewState(
                searchState = SearchState.History(emptyList())
            )).copy(searchState = SearchState.History(historyTracks))
        )
    }

    private fun triggerHideKeyboard() {
        _state.postValue(
            _state.value?.copy(
                uiEvent = _state.value!!.uiEvent.copy(hideKeyboard = true)
            ) ?: return
        )
    }

    fun onTrackClicked(track: Track) {
        if (!trackClickDebounce()) return

        tracksInteractor.saveTrack(track)
        _state.postValue(
            _state.value?.copy(
                uiEvent = _state.value!!.uiEvent.copy(navigateToTrack = track)
            ) ?: return
        )
    }

    fun clearSearch() {
        val currentState = _state.value ?: SearchViewState()
        _state.value = currentState.copy(
            searchQuery = "",
            searchState = SearchState.History(tracksInteractor.getTrackSearchHistory())
        )

        lastSearchQuery = ""
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        triggerHideKeyboard()
    }

    fun clearHistory() {
        tracksInteractor.clearTrackSearchHistory()
        showHistory()
    }

    fun loadHistory() {
        if (_state.value?.searchQuery.isNullOrEmpty()) {
            showHistory()
        }
    }

    fun clearEvents() {
        val currentState = _state.value ?: return
        if (currentState.uiEvent != SearchViewState.UiEvents()) {
            _state.postValue(currentState.copy(uiEvent = SearchViewState.UiEvents()))
        }
    }
}
