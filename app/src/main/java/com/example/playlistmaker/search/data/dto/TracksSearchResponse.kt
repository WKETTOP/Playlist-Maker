package com.example.playlistmaker.search.data.dto

class TracksSearchResponse(
    val resultCount: String,
    val expression: String,
    val results: List<TrackDto>
) : Response()
