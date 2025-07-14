package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.model.PlaylistUiModel
import com.example.playlistmaker.library.ui.model.PlaylistViewState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()

    private lateinit var playlistAdapter: PlaylistAdapter

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistAdapter = PlaylistAdapter { playlist ->
            val bundle = bundleOf("playlistId" to playlist.playlistId)
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_onePlaylistFragment,
                bundle
            )
        }

        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = playlistAdapter

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_createPlaylistFragment,
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistViewState.collect { state ->
                    when (state) {
                        is PlaylistViewState.Empty -> showEmpty()
                        is PlaylistViewState.Content -> showContent(state.playlists)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEmpty() {
        binding.playlistRecyclerView.isVisible = false
        binding.errorImage.isVisible = true
        binding.errorText.isVisible = true
        binding.newPlaylistButton.isVisible = true

        binding.errorImage.setImageResource(R.drawable.nothing_found_120)
        binding.errorText.setText(R.string.playlists_empty_line)
    }

    private fun showContent(playlists: List<PlaylistUiModel>) {
        binding.playlistRecyclerView.isVisible = true
        binding.errorImage.isVisible = false
        binding.errorText.isVisible = false
        binding.newPlaylistButton.isVisible = true

        playlistAdapter.updateData(playlists)
    }

    companion object {
        fun createArgs() = PlaylistsFragment()
    }
}
