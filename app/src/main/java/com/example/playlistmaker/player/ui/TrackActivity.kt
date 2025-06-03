package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Transform
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TrackActivity : AppCompatActivity() {

    companion object {
        const val TRACK_READ = "TRACK"
    }

    private lateinit var binding: ActivityTrackBinding

    private val viewModel by viewModel<TrackViewModel> { parametersOf(getTrackFromIntent()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerViewState.collectLatest { state ->
                    updatePlayerState(state.playerState)

                    when (state.playerState) {
                        TrackViewModel.PlayerState.PLAYING, TrackViewModel.PlayerState.PAUSED -> {
                            updateTrackPosition(state.currentPosition)
                        }

                        TrackViewModel.PlayerState.PREPARED -> {
                            if (state.currentPosition == "00:00") {
                                updateTrackPosition(state.currentPosition)
                            }
                        }

                        TrackViewModel.PlayerState.LOADING -> {}
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.playerViewState.value.playerState == TrackViewModel.PlayerState.PLAYING) {
            viewModel.togglePlayback()
        }
    }

    private fun updatePlayerState(state: TrackViewModel.PlayerState) {
        when (state) {
            TrackViewModel.PlayerState.LOADING -> {
                binding.playButton.isEnabled = false
                binding.playButton.setImageResource(R.drawable.play_button_100)
            }

            TrackViewModel.PlayerState.PREPARED -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.play_button_100)
            }

            TrackViewModel.PlayerState.PLAYING -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.stop_button_100)
            }

            TrackViewModel.PlayerState.PAUSED -> {
                binding.playButton.isEnabled = true
                binding.playButton.setImageResource(R.drawable.play_button_100)
            }
        }
    }

    private fun updateTrackPosition(position: String) {
        binding.trackTime.text = Transform.millisToMin(position)
    }

    private fun getTrackFromIntent(): Track {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_READ, Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_READ)!!
        }
    }
}
