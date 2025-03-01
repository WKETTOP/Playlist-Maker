package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsToolbar: Toolbar
    private lateinit var themeSwitch: SwitchMaterial
    private lateinit var shareButton: MaterialTextView
    private lateinit var supportButton: MaterialTextView
    private lateinit var userAgreementButton: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting_page)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsToolbar = findViewById(R.id.settings_toolbar)
        themeSwitch = findViewById(R.id.dark_theme_switch)
        shareButton = findViewById(R.id.share_button)
        supportButton = findViewById(R.id.support_button)
        userAgreementButton = findViewById(R.id.user_agreement_button)

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

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val darkTheme = sharedPreferences.getBoolean(App.DARK_THEME_KEY, false)
        themeSwitch.isChecked = darkTheme

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean(App.DARK_THEME_KEY, isChecked)
                .apply()
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}
