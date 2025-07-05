package com.example.playlistmaker.library.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DataBaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS 'playlist_table' (
                playlistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                coverImagePath TEXT NOT NULL,
                trackIds TEXT NOT NULL,
                trackCount INTEGER NOT NULL
            )
        """.trimIndent())
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS 'playlist_tracks_table' (
                trackId TEXT NOT NULL PRIMARY KEY,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                formattedTrackTime TEXT NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT NOT NULL DEFAULT '',
                formattedReleaseDate TEXT NOT NULL,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL
            )
        """.trimIndent())
        }
    }
}
