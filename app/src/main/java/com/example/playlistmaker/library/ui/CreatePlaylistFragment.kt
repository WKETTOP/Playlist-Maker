package com.example.playlistmaker.library.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private var titleTextWatcher: TextWatcher? = null
    private var descriptionTextWatcher: TextWatcher? = null

    private val pickVisualMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            if (_binding != null) {
                binding.playlistCoverImage.isVisible = true
                binding.addPhotoIcon.isVisible = false
                binding.playlistCoverImage.setImageURI(uri)
                viewModel.saveCoverImage(uri)
            }
        } ?: run {
            binding.playlistCoverImage.isVisible = false
            binding.addPhotoIcon.isVisible = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistToolbar.setNavigationOnClickListener {
            handleBackPressed()
        }

        titleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.updatePlaylistTitle(s?.toString().orEmpty())
                if (!binding.newPlaylistTitleInput.hasFocus()) {
                    binding.createNewPlaylistButton.isEnabled = !s.isNullOrEmpty()
                }
                binding.newPlaylistTitleLabel.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}

        }
        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.updatePlaylistDescription(s?.toString().orEmpty())
                if (!binding.newPlaylistDescriptionInput.hasFocus()) {
                    binding.newPlaylistDescriptionInput.isEnabled = !s.isNullOrEmpty()
                }
                binding.newPlaylistDescriptionLabel.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}

        }
        binding.newPlaylistTitleInput.addTextChangedListener(titleTextWatcher)
        binding.newPlaylistDescriptionInput.addTextChangedListener(descriptionTextWatcher)

        binding.addPhotoButton.setOnClickListener {
            pickVisualMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        binding.createNewPlaylistButton.setOnClickListener {
            viewModel.createPlaylist()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.screenState.collect { state ->
                        binding.createNewPlaylistButton.isEnabled = state.isCreateButtonEnabled
                    }
                }
                launch {
                    viewModel.playlistCreated.collect { playlistTitle ->
                        playlistTitle?.let {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.playlist_created_message_line, it),
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.playlistCreateMessageShow()

                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "playlist_created",
                                true
                            )
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        titleTextWatcher?.let {
            binding.newPlaylistTitleInput.removeTextChangedListener(it)
        }
        descriptionTextWatcher?.let {
            binding.newPlaylistDescriptionInput.removeTextChangedListener(it)
        }
        _binding = null
    }

    private fun handleBackPressed() {
        if (viewModel.hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_creating_playlist_title_line))
            .setMessage(getString(R.string.unsaved_data_line))
            .setPositiveButton(getString(R.string.finish_line)) { _, _ ->
                findNavController().navigateUp()
            }
            .setNeutralButton(getString(R.string.cancel_line)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
