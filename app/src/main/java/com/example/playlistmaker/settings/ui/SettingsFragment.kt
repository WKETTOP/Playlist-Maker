package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isDarkThemeEnabled.collectLatest { isDark ->
                    binding.darkThemeSwitch.isChecked = isDark
                }
            }
        }

        binding.shareButton.setOnClickListener {
            viewModel.onShareClicked()
        }

        binding.supportButton.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel.onUserAgreementClicked()
        }

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (binding.darkThemeSwitch.isPressed) {
                viewModel.onThemeSwitched(isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}