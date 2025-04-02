package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.impl.Transform

class TrackActivity : AppCompatActivity() {

    companion object {
        const val TRACK_READ = "TRACK"
        private const val TRACK_PLAYING_DELAY = 500L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

    }

    private var playerState = STATE_DEFAULT

    private var handler = Handler(Looper.getMainLooper())
    private var trackCurrentPosition: Int = 0
    private var trackFullTime: String = "0"

    private lateinit var trackPlayerInteractor: TrackPlayerInteractor

    private lateinit var trackTimeRunnable: Runnable
    private lateinit var url: String

    private lateinit var backButton: Toolbar
    private lateinit var artWork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var addButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var trackTime: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.big_track_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trackPlayerInteractor = Creator.provideTrackPlayer()

        backButton = findViewById(R.id.back_button)
        artWork = findViewById(R.id.art_work)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        addButton = findViewById(R.id.add_button)
        playButton = findViewById(R.id.play_button)
        favoriteButton = findViewById(R.id.favorite_button)
        trackTime = findViewById(R.id.track_time)
        durationValue = findViewById(R.id.duration_value)
        albumValue = findViewById(R.id.album_value)
        yearValue = findViewById(R.id.year_value)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)

        backButton.setNavigationOnClickListener {
            finish()
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_READ, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_READ)
        }

        if (track != null) {
            val cornerRadiusTrack = Transform.dpToPx(8f, this)

            url = track.previewUrl

            Glide.with(this)
                .load(track.getCoverArtWork())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadiusTrack))
                .into(artWork)
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.formattedTrackTime
            durationValue.text = track.formattedTrackTime
            albumValue.text = track.collectionName
            yearValue.text = track.formattedReleaseDate
            genreValue.text = track.primaryGenreName
            countryValue.text = track.country
        }

        preparePlayer()

        playButton.setOnClickListener {
            playerControl()
        }

        trackTimeRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    trackCurrentPosition = trackPlayerInteractor.getCurrentPosition()
                    trackTime.text = Transform.millisToMin(trackCurrentPosition.toString())
                    handler.postDelayed(this, TRACK_PLAYING_DELAY)
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(trackTimeRunnable)
        trackPlayerInteractor.releasePlayback()
    }


    private fun preparePlayer() {
        trackPlayerInteractor.prepareTrack(url) {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        trackPlayerInteractor.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button_100)
            playerState = STATE_PREPARED
            handler.removeCallbacks(trackTimeRunnable)
            trackTime.text = trackFullTime
        }
    }

    private fun startPlayer() {
        trackPlayerInteractor.startPlayback()
        playButton.setImageResource(R.drawable.stop_button_100)
        playerState = STATE_PLAYING
        handler.post(trackTimeRunnable)
    }

    private fun pausePlayer() {
        trackPlayerInteractor.pausePlayback()
        playButton.setImageResource(R.drawable.play_button_100)
        playerState = STATE_PAUSED
        handler.removeCallbacks(trackTimeRunnable)
    }

    private fun playerControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
}
