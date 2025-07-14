package com.example.playlistmaker.library.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.model.OnePlaylistUiModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val playlistData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("playlistData", OnePlaylistUiModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("playlistData")
        }

        if (playlistData == null) {
            findNavController().navigateUp()
            return
        }

        viewModel.initializeForEdit(playlistData)

        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistToolbar.title = getString(R.string.edit_playlist_line)
        binding.createNewPlaylistButton.text = getString(R.string.save_editing_playlist_line)

        fillFieldsWithData(playlistData)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistEdited.collect { playlistTitle ->
                    playlistTitle?.let {
                        viewModel.playlistUpdateMessageShow()
                        findNavController().navigateUp()
                    }
                }
            }
        }

        binding.createNewPlaylistButton.setOnClickListener {
            viewModel.updatePlaylist()
        }
    }

    private fun fillFieldsWithData(playlistData: OnePlaylistUiModel) {
        binding.newPlaylistTitleInput.setText(playlistData.title)
        binding.newPlaylistDescriptionInput.setText(playlistData.description)

        binding.newPlaylistTitleLabel.isVisible = playlistData.title.isNotEmpty()
        binding.newPlaylistDescriptionLabel.isVisible = playlistData.description.isNotEmpty()

        playlistData.coverUri?.let { uri ->
            binding.playlistCoverImage.isVisible = true
            binding.addPhotoIcon.isVisible = false
            binding.playlistCoverImage.setImageURI(uri)
        }
    }

    override fun handleBackPressed() {
        findNavController().navigateUp()
    }
}