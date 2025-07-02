package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.model.PlaylistViewState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun createArgs() = PlaylistsFragment()
    }

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

        }

        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = playlistAdapter

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_createPlaylistFragment,
            )
        }

        lifecycleScope.launch {
            viewModel.playlistViewState.collect { state ->
                when(state) {
                    is PlaylistViewState.Empty -> showEmpty()
                    is PlaylistViewState.Content -> showContent(state.playlists)
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

    private fun showContent(playlists: List<Playlist>) {
        binding.playlistRecyclerView.isVisible = true
        binding.errorImage.isVisible = false
        binding.errorText.isVisible = false
        binding.newPlaylistButton.isVisible = true

        playlistAdapter.updateData(playlists)
    }
}
