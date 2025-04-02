package com.example.playlistmaker.search.data.dto

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.TrackSearchHistory
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesTrackSearchHistory(private val prefs: SharedPreferences) :
    TrackSearchHistory {

    companion object {
        const val HISTORY_KEY = "track_search_history"
        private const val TRACK_SEARCH_MAX_SIZE = 10
    }


    override fun getTrackSearchHistory(): ArrayList<Track> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type) ?: ArrayList()
    }


    override fun saveTrack(track: Track) {
        val historyTrack = getTrackSearchHistory()

        historyTrack.removeAll { it.trackId == track.trackId }
        historyTrack.add(0, track)

        if (historyTrack.size > TRACK_SEARCH_MAX_SIZE) {
            historyTrack.removeAt(historyTrack.size - 1)
        }

        saveTrackSearchHistory(historyTrack, prefs)
    }

    private fun saveTrackSearchHistory(trackHistory: ArrayList<Track>, prefs: SharedPreferences) {
        val json = Gson().toJson(trackHistory)
        prefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    override fun clearTrackSearchHistory() {
        prefs.edit()
            .remove(HISTORY_KEY)
            .apply()
    }
}
