package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.ui.model.PlayerViewState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackViewModel(
    private val trackPlayerInteractor: TrackPlayerInteractor,
    private val _track: Track,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    enum class PlayerState {
        LOADING,
        PREPARED,
        PLAYING,
        PAUSED
    }

    companion object {
        private const val TRACK_PLAYING_DELAY = 500L
    }

    val track: Track
        get() = _track

    private val _playerViewState = MutableStateFlow(PlayerViewState())
    val playerViewState: StateFlow<PlayerViewState> = _playerViewState.asStateFlow()

    private val _isFavorite = MutableStateFlow(track.isFavorite)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var updateJob: Job? = null

    init {
        _playerViewState.value = PlayerViewState(
            playerState = PlayerState.LOADING,
            currentPosition = _track.formattedTrackTime
        )
        preparePlayer()
    }

    fun togglePlayback() {
        when (_playerViewState.value.playerState) {
            PlayerState.PLAYING -> pausePlayer()
            PlayerState.PAUSED, PlayerState.PREPARED -> startPlayer()
            else -> {}
        }
    }

    fun onFavoriteClick() {
        viewModelScope.launch {
            val isCurrentFavorite = _isFavorite.value

            if (isCurrentFavorite) {
                favoriteTracksInteractor.deleteTrackFromFavorite(track)
            } else {
                favoriteTracksInteractor.addTrackToFavorite(track)
            }

            _isFavorite.value = !isCurrentFavorite
        }
    }

    private fun preparePlayer() {
        updateViewState(playerState = PlayerState.LOADING)
        trackPlayerInteractor.prepareTrack(track.previewUrl) {
            updateViewState(
                playerState = PlayerState.PREPARED,
                currentPosition = track.formattedTrackTime
            )
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
        val current = _playerViewState.value
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
