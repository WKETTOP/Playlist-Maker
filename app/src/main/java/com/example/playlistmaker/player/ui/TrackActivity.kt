package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Transform

class TrackActivity : AppCompatActivity() {

    companion object {
        const val TRACK_READ = "TRACK"
    }

    private lateinit var binding: ActivityTrackBinding

    private val viewModel: TrackViewModel by viewModels {
        TrackViewModel.getViewModelFactory(getTrackFromIntent())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.big_track_view)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backButton.setNavigationOnClickListener {
            finish()
        }

        binding.playButton.setOnClickListener {
            viewModel.togglePlayback()
        }

        Glide.with(this)
            .load(viewModel.track.getCoverArtWork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(Transform.dpToPx(8f, this)))
            .into(binding.artWork)
        binding.trackName.text = viewModel.track.trackName
        binding.artistName.text = viewModel.track.artistName
        binding.trackTime.text = viewModel.track.formattedTrackTime
        binding.durationValue.text = viewModel.track.formattedTrackTime
        binding.albumValue.text = viewModel.track.collectionName
        binding.yearValue.text = viewModel.track.formattedReleaseDate
        binding.genreValue.text = viewModel.track.primaryGenreName
        binding.countryValue.text = viewModel.track.country

        viewModel.observePlayerState().observe(this) { state ->
            when (state) {
                TrackViewModel.PlayerState.LOADING -> showLoadingState()
                TrackViewModel.PlayerState.PREPARED -> showPrepareState()
                TrackViewModel.PlayerState.PLAYING -> showPlayingState()
                TrackViewModel.PlayerState.PAUSED -> showPausedState()
            }
        }

        viewModel.observeCurrentPosition().observe(this) { position ->
            binding.trackTime.text = Transform.millisToMin(position.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.observePlayerState().value == TrackViewModel.PlayerState.PLAYING) {
            viewModel.togglePlayback()
        }
    }

    private fun getTrackFromIntent(): Track {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_READ, Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_READ)!!
        }
    }

    private fun showLoadingState() {
        binding.playButton.isEnabled = false
        binding.playButton.setImageResource(R.drawable.play_button_100)
    }

    private fun showPrepareState() {
        binding.playButton.isEnabled = true
        binding.playButton.setImageResource(R.drawable.play_button_100)
    }

    private fun showPlayingState() {
        binding.playButton.setImageResource(R.drawable.stop_button_100)
    }

    private fun showPausedState() {
        binding.playButton.setImageResource(R.drawable.play_button_100)
    }
}
