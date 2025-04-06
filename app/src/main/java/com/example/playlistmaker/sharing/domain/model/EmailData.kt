package com.example.playlistmaker.sharing.domain.model

data class EmailData(
    val recipient: String,
    val subject: String,
    val body: String
)
