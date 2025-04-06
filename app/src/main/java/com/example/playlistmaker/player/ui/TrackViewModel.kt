package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.ui.model.PlayerViewState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Creator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackViewModel(
    private val trackPlayerInteractor: TrackPlayerInteractor,
    private val _track: Track
) : ViewModel() {

    enum class PlayerState {
        LOADING,
        PREPARED,
        PLAYING,
        PAUSED
    }

    companion object {
        private const val TRACK_PLAYING_DELAY = 500L

        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackViewModel(
                    trackPlayerInteractor = Creator.provideTrackPlayer(),
                    _track = track
                )
            }
        }
    }

    val track: Track
        get() = _track

    private val _playerViewState = MutableLiveData<PlayerViewState>()
    val playerViewState: LiveData<PlayerViewState> = _playerViewState

    private var updateJob: Job? = null

    init {
        _playerViewState.value = PlayerViewState(
            playerState = PlayerState.LOADING,
            currentPosition = _track.formattedTrackTime
        )
        preparePlayer()
    }

    fun togglePlayback() {
        when (_playerViewState.value?.playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PAUSED, PlayerState.PREPARED -> startPlayer()
            else -> {}
        }
    }

    private fun preparePlayer() {
        updateViewState(playerState = PlayerState.LOADING)
        trackPlayerInteractor.prepareTrack(track.previewUrl) {
            updateViewState(playerState = PlayerState.PREPARED, currentPosition = track.formattedTrackTime)
        }
        trackPlayerInteractor.setOnCompletionListener {
            updateViewState(playerState = PlayerState.PREPARED, currentPosition = "00:00")
            updateJob?.cancel()
        }
    }

    private fun startPlayer() {
        trackPlayerInteractor.startPlayback()
        updateViewState(playerState = PlayerState.PLAYING)
        startPositionUpdate()
    }

    private fun pausePlayer() {
        trackPlayerInteractor.pausePlayback()
        updateViewState(playerState = PlayerState.PAUSED)
        stopPositionUpdate()
    }

    private fun startPositionUpdate() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                val position = trackPlayerInteractor.getCurrentPosition()
                updateViewState(currentPosition = position.toString())
                delay(TRACK_PLAYING_DELAY)
            }
        }
    }

    private fun stopPositionUpdate() {
        updateJob?.cancel()
    }

    private fun updateViewState(
        playerState: PlayerState? = null,
        currentPosition: String? = null
    ) {
        val current = _playerViewState.value ?: PlayerViewState()
        _playerViewState.value = current.copy(
            playerState = playerState ?: current.playerState,
            currentPosition = currentPosition ?: current.currentPosition
        )
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayerInteractor.releasePlayback()
        updateJob?.cancel()
    }
}
