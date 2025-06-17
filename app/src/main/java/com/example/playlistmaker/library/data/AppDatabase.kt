package com.example.playlistmaker.library.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.data.dao.TrackDao
import com.example.playlistmaker.library.data.db.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
}
