package com.example.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.TrackEntity

@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId LIMIT 1)")
    suspend fun getTrackId(trackId: String): Boolean

    @Query("SELECT * FROM track_table ORDER BY rowid DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM track_table")
    suspend fun getTracksId(): List<String>
}
