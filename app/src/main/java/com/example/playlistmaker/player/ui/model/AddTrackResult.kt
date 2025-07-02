package com.example.playlistmaker.player.ui.model

sealed interface AddTrackResult {

    data class Success(val playlistTitle: String) : AddTrackResult

    data class AlreadyExists(val playlistTitle: String) : AddTrackResult
}