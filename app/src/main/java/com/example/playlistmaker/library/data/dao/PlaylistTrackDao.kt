package com.example.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId = :trackId LIMIT 1")
    suspend fun getTrack(trackId: String): PlaylistTrackEntity

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId IN (:trackIds)")
    suspend fun getTracksByIds(trackIds: List<String>): List<PlaylistTrackEntity>
}