package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface TrackClickListener {
    fun onTrackClicked(track: Track)
}
