package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator): SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getSharedAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getSharedAppLink(): String {
        return "https://practicum.yandex.ru/android-developer/"
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            recipient = "praktikum@support.yandex.ru",
            subject = "Сообщение разработчикам Playlist Maker",
            body = "Спасибо разработчикам!"
        )
    }

    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/"
    }
}
