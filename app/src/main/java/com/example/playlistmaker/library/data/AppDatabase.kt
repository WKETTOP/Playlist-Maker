package com.example.playlistmaker.library.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.data.dao.PlaylistDao
import com.example.playlistmaker.library.data.dao.PlaylistTrackDao
import com.example.playlistmaker.library.data.dao.TrackDao
import com.example.playlistmaker.library.data.db.PlaylistEntity
import com.example.playlistmaker.library.data.db.PlaylistTrackEntity
import com.example.playlistmaker.library.data.db.TrackEntity

@Database(
    version = 3,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao
}
