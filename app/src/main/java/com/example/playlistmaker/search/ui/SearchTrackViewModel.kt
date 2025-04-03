package com.example.playlistmaker.search.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Creator

class SearchTrackViewModel(
    private val tracksInteractor: TracksInteractor,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchTrackViewModel(
                    tracksInteractor = Creator.provideTracksInteractor(),
                    application = this[APPLICATION_KEY] as Application)
            }
        }
    }

    private var handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val history = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = history

    private val navigateToTrack = SingleLiveEvent<Track>()
    fun observeNavigateToTrack(): LiveData<Track> = navigateToTrack

    private val showToast = SingleLiveEvent<String>()
    fun observerShowToast(): LiveData<String> = showToast

    private var lastSearchQuery: String? = null
    private var isTrackClickAllowed = true

    init {
        loadHistory()
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
        if (query.isNotEmpty()) {

            stateLiveData.value = SearchState.Loading

            tracksInteractor.searchTrack(
                query,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                       when {
                           errorMessage != null -> handlerError(errorMessage)
                           foundTracks.isNullOrEmpty() -> handleEmptyResults()
                           else -> handleSuccess(foundTracks)
                       }
                    }

                }
            )
        }
    }

    private fun trackClickDebounce(): Boolean {
        val current = isTrackClickAllowed
        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            handler.postDelayed({ isTrackClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun onTrackClicked(track: Track) {
        if (trackClickDebounce()) {
            tracksInteractor.saveTrack(track)
            navigateToTrack.value = track
        }
    }

    fun clearHistory() {
        tracksInteractor.clearTrackSearchHistory()
        loadHistory()
    }

    private fun loadHistory() {
        history.value = tracksInteractor.getTrackSearchHistory()
    }

    private fun handlerError(message: String) {
        stateLiveData.value = SearchState.Error(
            getApplication<Application>().getString(R.string.communication_problems)
        )
        showToast.value = message
    }

    private fun handleEmptyResults() {
        stateLiveData.value = SearchState.Empty(
            getApplication<Application>().getString(R.string.nothing_found)
        )
    }

    private fun handleSuccess(track: List<Track>) {
        stateLiveData.value = SearchState.Content(track.toMutableList())
    }
}
