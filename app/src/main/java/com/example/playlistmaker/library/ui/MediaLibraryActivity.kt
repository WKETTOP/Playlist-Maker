package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.mediaLibraryViewPager.adapter = MediaLibraryViewPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle
        )

        tabLayoutMediator = TabLayoutMediator(
            binding.mediaLibraryTabLayout,
            binding.mediaLibraryViewPager
        ) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite_track_line)
                1 -> tab.text = getString(R.string.playlists_line)
            }
        }
        tabLayoutMediator.attach()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}
