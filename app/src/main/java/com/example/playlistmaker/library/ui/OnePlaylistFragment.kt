package com.example.playlistmaker.library.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentOnePlaylistBinding
import com.example.playlistmaker.library.ui.model.OnePlaylistUiModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnePlaylistFragment : Fragment() {

    private var _binding: FragmentOnePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<OnePlaylistViewModel>()

    private lateinit var trackAdapter: TrackAdapter
    private var playlistId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getInt("playlistId") ?: run {
            navigateBack()
            return
        }

        trackAdapter = TrackAdapter(
            onClick = { track ->
                val bundle = bundleOf("TRACK" to track)
                findNavController().navigate(
                    R.id.action_onePlaylistFragment_to_trackFragment,
                    bundle
                )
            },
            onLongItemClick = { track ->
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.delete_track_line)
                    .setNegativeButton(R.string.no_line) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.yes_line) { _, _ ->
                        viewModel.removeTrackFromPlaylist(track, playlistId)
                    }
                    .show()
            }
        )

        binding.backButton.setNavigationOnClickListener {
            navigateBack()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.onePlaylistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            val screenHeight = resources.displayMetrics.heightPixels
            peekHeight = (screenHeight * 0.38).toInt()
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.isVisible = true
                        binding.shareButton.isClickable = false
                        binding.moreButton.isClickable = false
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction {
                                binding.overlay.isVisible = false
                            }
                            .start()
                        binding.shareButton.isClickable = true
                        binding.moreButton.isClickable = true
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.isVisible = true

                val alpha = when {
                    slideOffset < 0f -> {
                        (slideOffset + 1f) * 0.6f
                    }
                    else -> 0.6f + slideOffset * 0.4f
                }
                binding.overlay.alpha = alpha
            }
        })

        val bottomSheetBehaviorEdit = BottomSheetBehavior.from(binding.onePlaylistBottomSheetEdit).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            val screenHeight = resources.displayMetrics.heightPixels
            peekHeight = (screenHeight * 0.49).toInt()
        }

        bottomSheetBehaviorEdit.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.isVisible = true
                        binding.shareButton.isClickable = false
                        binding.moreButton.isClickable = false
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        binding.onePlaylistBottomSheet.isVisible = true
                        binding.shareButton.isClickable = true
                        binding.moreButton.isClickable = true
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.isVisible = true

                val alpha = when {
                    slideOffset < 0f -> {
                        (slideOffset + 1f) * 0.6f
                    }
                    else -> 0.6f + slideOffset * 0.4f
                }
                binding.overlay.alpha = alpha
            }
        })

        binding.overlay.apply {
            alpha = 0f

            setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                if (bottomSheetBehaviorEdit.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehaviorEdit.state = BottomSheetBehavior.STATE_HIDDEN
                    binding.onePlaylistBottomSheet.isVisible = true
                }
            }
        }

        binding.tracksOnPlaylistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksOnPlaylistRecyclerView.adapter = trackAdapter

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.moreButton.setOnClickListener {
            if (bottomSheetBehaviorEdit.state != BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehaviorEdit.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.onePlaylistBottomSheet.isVisible = false
            } else {
                bottomSheetBehaviorEdit.state = BottomSheetBehavior.STATE_HIDDEN
                binding.onePlaylistBottomSheet.isVisible = true
            }
        }

        binding.shareLineButton.setOnClickListener {
            sharePlaylist()
        }

        binding.deletePlaylistButton.setOnClickListener {
            showDeleteDialog()
        }

        binding.editButton.setOnClickListener {
            val currentPlaylist = viewModel.playlistState.value
            if (currentPlaylist != null) {
                val bundle = bundleOf("playlistData" to currentPlaylist)
                findNavController().navigate(
                    R.id.action_onePlaylistFragment_to_editPlaylistFragment,
                    bundle
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistState.collect { playlist ->
                    playlist?.let { updateUi(it) }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylist(playlistId)

        val bottomSheetBehaviorEdit = BottomSheetBehavior.from(binding.onePlaylistBottomSheetEdit)

        when {
            bottomSheetBehaviorEdit.state == BottomSheetBehavior.STATE_COLLAPSED -> {
                binding.overlay.isVisible = true
                binding.overlay.alpha = 0.6f
                binding.shareButton.isClickable = false
                binding.moreButton.isClickable = false
                binding.onePlaylistBottomSheet.isVisible = false
            }
            else -> {
                binding.overlay.isVisible = false
                binding.overlay.alpha = 0f
                binding.shareButton.isClickable = true
                binding.moreButton.isClickable = true
            }
        }
    }

    private fun updateUi(playlist: OnePlaylistUiModel) {
        binding.apply {
            onePlaylistTitle.text = playlist.title
            playlistMenuTitle.text = playlist.title

            if (playlist.description.isEmpty()) {
                onePlaylistDescription.isVisible = false
            } else {
                onePlaylistDescription.isVisible = true
                onePlaylistDescription.text = playlist.description
            }

            playlistTime.text = playlist.totalDuration

            playlistTracksNumber.text = playlist.trackCount
            playlistMenuTrackCount.text = playlist.trackCount

            setPlaylistCover(playlist.coverUri)

            updateTracksList(playlist.tracks)
        }
    }

    private fun setPlaylistCover(coverUri: Uri?) {
        if (coverUri != null) {
            Glide.with(this@OnePlaylistFragment)
                .load(coverUri)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(binding.onePlaylistCoverImage)
            Glide.with(this@OnePlaylistFragment)
                .load(coverUri)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(binding.playlistMenuCover)
        } else {
            binding.onePlaylistCoverImage.setImageResource(R.drawable.placeholder)
            binding.playlistMenuCover.setImageResource(R.drawable.placeholder)
        }
    }

    private fun updateTracksList(tracks: List<Track>) {
        trackAdapter.updateData(tracks)
        binding.tracksOnPlaylistRecyclerView.isVisible = tracks.isNotEmpty()
    }

    private fun sharePlaylist() {
        val playlist = viewModel.playlistState.value
        if (playlist?.tracks.isNullOrEmpty()) {
            Toast.makeText(requireContext(), R.string.empty_playlist_line, Toast.LENGTH_LONG).show()
        } else {
            val shareText = buildShareText(playlist)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Playlist"))
        }
    }

    private fun buildShareText(playlist: OnePlaylistUiModel): String {
        val sb = StringBuilder()
        sb.append("${playlist.title}\n${playlist.description}\n${playlist.trackCount}")
        playlist.tracks.forEachIndexed { index, track ->
            sb.append("${index + 1}, ${track.trackName} - ${track.artistName} (${track.formattedTrackTime}\n)")
        }
        return sb.toString()
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist_line)
            .setMessage(R.string.delete_playlist_question)
            .setNegativeButton(R.string.no_line) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes_line) { _, _ ->
                viewModel.deletePlaylist(playlistId)
                findNavController().navigate(R.id.action_onePlaylistFragment_to_mediaLibraryFragment)
            }
            .show()
    }

    private fun navigateBack() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
