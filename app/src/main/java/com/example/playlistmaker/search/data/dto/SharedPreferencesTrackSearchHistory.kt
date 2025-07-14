package com.example.playlistmaker.search.data.dto

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.library.data.AppDatabase
import com.example.playlistmaker.search.domain.TrackSearchHistory
import com.example.playlistmaker.search.domain.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesTrackSearchHistory(
    private val prefs: SharedPreferences,
    private val appDatabase: AppDatabase
) : TrackSearchHistory {

    companion object {
        const val HISTORY_KEY = "track_search_history"
        private const val TRACK_SEARCH_MAX_SIZE = 10
    }

    override suspend fun getTrackSearchHistory(): List<Track> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        val tracks: List<Track> = Gson().fromJson(json, type) ?: emptyList()

        val tracksIdInFavorite = appDatabase.favoriteTrackDao().getTracksId().toSet()

        return tracks.map { track ->
            track.copy(isFavorite = track.trackId in tracksIdInFavorite)
        }
    }


    override suspend fun saveTrack(track: Track) {
        val historyTrack = getTrackSearchHistory().toMutableList()

        historyTrack.removeAll { it.trackId == track.trackId }
        historyTrack.add(0, track)

        if (historyTrack.size > TRACK_SEARCH_MAX_SIZE) {
            historyTrack.removeAt(historyTrack.size - 1)
        }

        saveTrackSearchHistory(historyTrack, prefs)
    }

    private fun saveTrackSearchHistory(trackHistory: List<Track>, prefs: SharedPreferences) {
        val json = Gson().toJson(trackHistory)
        prefs.edit {
            putString(HISTORY_KEY, json)
        }
    }

    override fun clearTrackSearchHistory() {
        prefs.edit {
            remove(HISTORY_KEY)
        }
    }
}
