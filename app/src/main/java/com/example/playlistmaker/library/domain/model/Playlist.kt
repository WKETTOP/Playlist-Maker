package com.example.playlistmaker.library.domain.model

data class Playlist(
    val playlistId: Int = 0,
    val title: String,
    val description: String,
    val coverImagePath: String,
    val trackIds: List<String>,
    val trackCount: Int
)
