package com.example.playlistmaker.data.dto

class TracksSearchResponse(
    val resultCount: String,
    val expression: String,
    val results: List<TrackDto>
) : Response()
