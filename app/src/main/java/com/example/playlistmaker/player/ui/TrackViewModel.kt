package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.dp.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.ui.model.AddTrackResult
import com.example.playlistmaker.player.ui.model.PlayerViewState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackViewModel(
    private val _track: Track,
    private val trackPlayerInteractor: TrackPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor
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
    val playerViewState: StateFlow<PlayerViewState> = _playerViewState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    private val _addTrackResult = MutableStateFlow<AddTrackResult?>(null)
    val addTrackResult: StateFlow<AddTrackResult?> = _addTrackResult

    private var updateJob: Job? = null
    private var favoriteJob: Job? = null

    init {
        _playerViewState.value = PlayerViewState(
            playerState = PlayerState.LOADING,
            currentPosition = _track.formattedTrackTime
        )
        preparePlayer()

        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            favoriteTracksInteractor.getTrackId(track.trackId).collect { isFavorite ->
                _isFavorite.value = isFavorite
            }
        }
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

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                _playlists.value = playlists
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val success = playlistInteractor.addTrackToPlaylist(_track, playlist)

            _addTrackResult.value = if (success) {
                AddTrackResult.Success(playlist.title)
            } else {
                AddTrackResult.AlreadyExists(playlist.title)
            }
        }
    }

    fun clearAddTrackResult() {
        _addTrackResult.value = null
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
        favoriteJob?.cancel()
    }
}
