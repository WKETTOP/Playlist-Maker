package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeManager
import com.example.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _isDarkThemeEnabled = MutableStateFlow(themeManager.isDarkThemeEnabledNow())
    val isDarkThemeEnabled: StateFlow<Boolean> = _isDarkThemeEnabled.asStateFlow()

    init {
        updateThemeState()
    }

    private fun updateThemeState() {
        _isDarkThemeEnabled.value = themeManager.isDarkThemeEnabledNow()
    }

    fun onThemeSwitched(isChecked: Boolean) {
        themeManager.switchThemeSettings(isChecked)
        updateThemeState()
        _isDarkThemeEnabled.value = isChecked
    }

    fun onShareClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onUserAgreementClicked() {
        sharingInteractor.openTerms()
    }

}
