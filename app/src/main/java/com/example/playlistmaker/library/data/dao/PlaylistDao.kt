package com.example.playlistmaker.library.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.playlistmaker.library.data.db.PlaylistEntity
import com.example.playlistmaker.library.data.db.PlaylistTrackCrossRef
import com.example.playlistmaker.library.data.db.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Transaction
    @Query("""
        SELECT p.*, pt.trackId
        FROM playlist_table p
        LEFT JOIN playlist_tracks pt ON p.playlistId = pt.trackId
        WHERE p.playlistId = :playlistId
        ORDER BY pt.addedAt DESC
    """)
    suspend fun getPlaylistWithTracks(playlistId: Int): PlaylistWithTracks?

    @Transaction
    @Query("""
        SELECT p.*, pt.trackId
        FROM playlist_table p
        LEFT JOIN playlist_tracks pt ON p.playlistId = pt.playlistId
        ORDER BY p.createdAt DESC, pt.addedAt DESC
    """)
    fun getAllPlaylistWithTracks(): Flow<List<PlaylistWithTracks>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(playlistTrack: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: String): Boolean

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTrackCount(playlistId: Int): Int

    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY addedAt DESC")
    suspend fun getTrackIdsFromPlaylist(playlistId: Int): List<String>

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)
}