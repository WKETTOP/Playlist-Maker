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

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

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
        _playerState.value = PlayerState.LOADING
        trackPlayerInteractor.prepareTrack(track.previewUrl) {
            _playerState.postValue(PlayerState.PREPARED)
        }
        trackPlayerInteractor.setOnCompletionListener {
            _playerState.postValue(PlayerState.PREPARED)
            updateJob?.cancel()
        }
    }

    private fun startPlayer() {
        trackPlayerInteractor.startPlayback()
        _playerState.value = PlayerState.PLAYING
        startPositionUpdate()
    }

    private fun pausePlayer() {
        trackPlayerInteractor.pausePlayback()
        _playerState.value = PlayerState.PAUSED
        stopPositionUpdate()
    }

    private fun startPositionUpdate() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                _currentPosition.postValue(trackPlayerInteractor.getCurrentPosition())
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
}
