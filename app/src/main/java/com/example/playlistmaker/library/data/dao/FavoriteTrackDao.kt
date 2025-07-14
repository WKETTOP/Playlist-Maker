package com.example.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.FavoriteTrackEntity

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(favoriteTrackEntity: FavoriteTrackEntity)

    @Delete
    suspend fun deleteTrack(favoriteTrackEntity: FavoriteTrackEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks WHERE trackId = :trackId LIMIT 1)")
    suspend fun getTrackId(trackId: String): Boolean

    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt DESC")
    suspend fun getTracks(): List<FavoriteTrackEntity>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getTracksId(): List<String>
}