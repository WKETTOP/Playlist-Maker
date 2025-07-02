package com.example.playlistmaker.player.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityTrackBinding
import com.example.playlistmaker.library.ui.CreatePlaylistActivity
import com.example.playlistmaker.player.ui.model.AddTrackResult
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Transform
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TrackActivity : AppCompatActivity() {

    companion object {
        const val TRACK_READ = "TRACK"
    }

    private lateinit var binding: ActivityTrackBinding
    private lateinit var playlistAdapter: BottomSheetPlaylistAdapter

    private val createPlaylistLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.loadPlaylists()
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

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


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            val screenHeight = resources.displayMetrics.heightPixels
            peekHeight = (screenHeight * 0.6).toInt()
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> binding.overlay.isVisible = false
                    else -> binding.overlay.isVisible = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = when {
                   slideOffset < 0f -> {
                       (slideOffset + 1f) * 0.6f
                   }
                   else -> 0.6f + slideOffset * 0.4f
                }
                binding.overlay.alpha = alpha
            }
        })

        playlistAdapter = BottomSheetPlaylistAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.availablePlaylistsRecyclerView.layoutManager =
            LinearLayoutManager(this@TrackActivity)
        binding.availablePlaylistsRecyclerView.adapter = playlistAdapter

        binding.playButton.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClick()
        }

        binding.addButton.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener {
            val intent = Intent(this, CreatePlaylistActivity::class.java)
            createPlaylistLauncher.launch(intent)
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
                launch {
                    viewModel.playerViewState.collect { state ->
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
                launch {
                    viewModel.isFavorite.collect { isFavorite ->
                        updateFavoriteButton(isFavorite)
                    }
                }
                launch {
                    viewModel.playlists.collect { playlists ->
                        playlistAdapter.updateData(playlists)
                    }
                }
                launch {
                    viewModel.addTrackResult.collect { result ->
                        result?.let {
                            showAddTrackToast(it)
                            viewModel.clearAddTrackResult()
                        }
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

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_in_favorite_button_51
        } else {
            R.drawable.favorite_button_51
        }

        binding.favoriteButton.setImageResource(iconRes)
    }

    private fun getTrackFromIntent(): Track {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_READ, Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_READ)!!
        }
    }

    private fun showAddTrackToast(result: AddTrackResult) {
        val message = when (result) {
            is AddTrackResult.Success -> getString(
                R.string.added_to_playlist_line,
                result.playlistTitle
            )

            is AddTrackResult.AlreadyExists -> getString(
                R.string.already_in_playlist_line,
                result.playlistTitle
            )
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
