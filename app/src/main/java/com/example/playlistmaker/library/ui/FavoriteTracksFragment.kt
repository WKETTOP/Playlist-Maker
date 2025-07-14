package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.library.ui.model.FavoriteTracksViewState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    private lateinit var trackAdapter: TrackAdapter

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(
            onClick = { track ->
                navigateToTrackPlayer(track)
            },
            onLongItemClick = { }
        )

        binding.favoriteTracks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoriteTracks.adapter = trackAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteTracksViewState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    private fun navigateToTrackPlayer(track: Track) {
        val args = Bundle().apply {
            putParcelable("TRACK", track)
        }

        findNavController().navigate(
            R.id.action_mediaLibraryFragment_to_trackFragment,
            args
        )
    }

    private fun renderState(state: FavoriteTracksViewState) {
        when (state) {
            is FavoriteTracksViewState.Empty -> showEmpty()
            is FavoriteTracksViewState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        binding.favoriteTracksProgress.isVisible = false
        binding.errorText.isVisible = true
        binding.errorImage.isVisible = true
        binding.favoriteTracks.isVisible = false

        trackAdapter.updateData(emptyList())
        binding.errorText.setText(R.string.favorite_tracks_empty_line)
        binding.errorImage.setImageResource(R.drawable.nothing_found_120)
    }

    private fun showContent(tracks: List<Track>) {
        binding.favoriteTracksProgress.isVisible = false
        binding.errorText.isVisible = false
        binding.errorImage.isVisible = false
        binding.favoriteTracks.isVisible = true
        trackAdapter.updateData(tracks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.favoriteTracks.adapter = null
        _binding = null
    }

    companion object {
        fun createArgs() = FavoriteTracksFragment()
    }
}
