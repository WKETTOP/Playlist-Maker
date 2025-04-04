package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
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

    private val playerState = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val currentPosition = MutableLiveData<Int>()
    fun observeCurrentPosition(): LiveData<Int> = currentPosition

    private var updateJob: Job? = null

    init {
        preparePlayer()
    }

    fun togglePlayback() {
        when (playerState.value) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PAUSED, PlayerState.PREPARED -> startPlayer()
            else -> {}
        }
    }

    private fun preparePlayer() {
        playerState.value = PlayerState.LOADING
        trackPlayerInteractor.prepareTrack(track.previewUrl) {
            playerState.postValue(PlayerState.PREPARED)
        }
        trackPlayerInteractor.setOnCompletionListener {
            playerState.postValue(PlayerState.PREPARED)
            updateJob?.cancel()
        }
    }

    private fun startPlayer() {
        trackPlayerInteractor.startPlayback()
        playerState.value = PlayerState.PLAYING
        startPositionUpdate()
    }

    private fun pausePlayer() {
        trackPlayerInteractor.pausePlayback()
        playerState.value = PlayerState.PAUSED
        stopPositionUpdate()
    }

    private fun startPositionUpdate() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                currentPosition.postValue(trackPlayerInteractor.getCurrentPosition())
                delay(TRACK_PLAYING_DELAY)
            }
        }
    }

    private fun stopPositionUpdate() {
        updateJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayerInteractor.releasePlayback()
        updateJob?.cancel()
    }

    enum class PlayerState {
        LOADING, PREPARED, PLAYING, PAUSED
    }
}
