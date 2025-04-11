package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setOnApplyWindowInsetsListener(findViewById(R.id.setting_page)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.isDarkThemeEnabled.observe(this) { isDark ->
            binding.darkThemeSwitch.isChecked = isDark
        }

        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
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
    }

    override fun onResume() {
        super.onResume()

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (binding.darkThemeSwitch.isPressed) {
                viewModel.onThemeSwitched(isChecked)
            }
        }
    }

}
