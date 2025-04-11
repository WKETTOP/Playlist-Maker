package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeManager
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> = _isDarkThemeEnabled

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
