package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.library.data.AppDatabase
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.search.data.dto.SharedPreferencesTrackSearchHistory
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.domain.TrackSearchHistory
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("track_search_history", Context.MODE_PRIVATE)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single { TrackDbConverter() }

    factory { Gson() }

    single<TrackSearchHistory> {
        SharedPreferencesTrackSearchHistory(get(), get())
    }

    single<NetworkClient> {
        RetrofitClient(androidContext(), get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}