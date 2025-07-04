package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentTrackBinding
import com.example.playlistmaker.player.ui.model.AddTrackResult
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Transform
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TrackFragment : Fragment() {

    private var _binding: FragmentTrackBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistAdapter: BottomSheetPlaylistAdapter

    private val viewModel by viewModel<TrackViewModel> { parametersOf(getTrackFromArguments()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setNavigationOnClickListener {
            findNavController().navigateUp()
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
            LinearLayoutManager(requireContext())
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
            findNavController().navigate(R.id.action_trackFragment_to_createPlaylistFragment)
        }

        Glide.with(this)
            .load(viewModel.track.getCoverArtWork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(Transform.dpToPx(8f, requireContext())))
            .into(binding.artWork)
        binding.trackName.text = viewModel.track.trackName
        binding.artistName.text = viewModel.track.artistName
        binding.trackTime.text = viewModel.track.formattedTrackTime
        binding.durationValue.text = viewModel.track.formattedTrackTime
        binding.albumValue.text = viewModel.track.collectionName
        binding.yearValue.text = viewModel.track.formattedReleaseDate
        binding.genreValue.text = viewModel.track.primaryGenreName
        binding.countryValue.text = viewModel.track.country

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("playlist_created")
            ?.observe(viewLifecycleOwner) { created ->
                if (created == true) {
                    viewModel.loadPlaylists()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                    findNavController().currentBackStackEntry?.savedStateHandle?.set("playlist_created", false)
                }
            }

        viewLifecycleOwner.lifecycleScope.launch {
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

    override fun onResume() {
        super.onResume()
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            binding.overlay.isVisible = true
            binding.overlay.alpha = 0.6f
        }
    }

    private fun getTrackFromArguments(): Track {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TRACK_READ, Track::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(TRACK_READ)!!
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

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TRACK_READ = "TRACK"
    }
}