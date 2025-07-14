package com.example.playlistmaker.library.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.data.dao.FavoriteTrackDao
import com.example.playlistmaker.library.data.dao.PlaylistDao
import com.example.playlistmaker.library.data.dao.PlaylistTrackDao
import com.example.playlistmaker.library.data.db.FavoriteTrackEntity
import com.example.playlistmaker.library.data.db.PlaylistEntity
import com.example.playlistmaker.library.data.db.PlaylistTrackCrossRef
import com.example.playlistmaker.library.data.db.PlaylistTrackEntity

@Database(
    version = 6,
    entities = [
        FavoriteTrackEntity::class,
        PlaylistTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    abstract fun playlistTrackDao(): PlaylistTrackDao

    abstract fun playlistDao(): PlaylistDao

}
