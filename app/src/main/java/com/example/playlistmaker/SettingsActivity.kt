package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting_page)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsToolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        val shareButton = findViewById<MaterialTextView>(R.id.share_button)
        val supportButton = findViewById<MaterialTextView>(R.id.support_button)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.user_agreement_button)

        settingsToolbar.setNavigationOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        shareButton.setOnClickListener {
            val urlAndroidDev = getString(R.string.url_android_dev)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, urlAndroidDev)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_line)))
        }

        supportButton.setOnClickListener {
            val email = getString(R.string.support_email)
            val subject = getString(R.string.support_email_subject)
            val body = getString(R.string.support_email_body)

            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(supportIntent)
        }

        userAgreementButton.setOnClickListener {
            val urlUserAgreement = getString(R.string.url_user_agreement)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlUserAgreement))
            startActivity(browserIntent)
        }
    }
}
