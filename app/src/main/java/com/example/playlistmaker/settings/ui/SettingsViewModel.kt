package com.example.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.ThemeManager
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val themeManager: ThemeManager,
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val isDarkThemeEnabled = MutableLiveData<Boolean>()
    fun observeThemeMode(): LiveData<Boolean> = isDarkThemeEnabled

    init {
        updateThemeState()
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    themeManager = Creator.provideThemeManager(),
                    sharingInteractor = Creator.provideSharingInteractor(context)
                )
            }
        }
    }

    private fun updateThemeState() {
         isDarkThemeEnabled.value = themeManager.isDarkThemeEnabledNow()
    }

    fun onThemeSwitched(isChecked: Boolean) {
        themeManager.switchThemeSettings(isChecked)
        updateThemeState()
        isDarkThemeEnabled.value = isChecked
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
