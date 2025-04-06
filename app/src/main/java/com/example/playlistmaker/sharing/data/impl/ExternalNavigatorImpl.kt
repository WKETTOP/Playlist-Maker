package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {

    override fun shareLink() {
        try {
            val url = context.getString(R.string.share_app_line)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, null))
        } catch (e: Throwable) {
            showErrorToast()
        }

    }

    override fun openLink() {
        try {
            val url = context.getString(R.string.url_user_agreement)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        } catch (e: Throwable) {
            showErrorToast()
        }

    }

    override fun openEmail() {
        val emailData = EmailData(
            recipient = context.getString(R.string.support_email),
            subject = context.getString(R.string.support_email_subject),
            body = context.getString(R.string.support_email_body)
        )
        try {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.recipient))
                putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
                putExtra(Intent.EXTRA_TEXT, emailData.body)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(supportIntent)
        } catch (e: Throwable) {
            showErrorToast()
        }

    }

    private fun showErrorToast() {
        val errorMessage = context.getString(R.string.app_not_found)
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

    }
}
