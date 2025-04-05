package com.example.playlistmaker.player.ui.model

import com.example.playlistmaker.player.ui.TrackViewModel

data class PlayerViewState(
    val playerState: TrackViewModel.PlayerState = TrackViewModel.PlayerState.LOADING,
    val currentPosition: String = "00:00"
)
