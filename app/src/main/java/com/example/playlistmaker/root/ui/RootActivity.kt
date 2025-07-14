package com.example.playlistmaker.root.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private var _binding: ActivityRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.rootFragmentContainerView.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.root_fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.trackFragment, R.id.createPlaylistFragment, R.id.onePlaylistFragment, R.id.editPlaylistFragment -> {
                    binding.bottomNavigationView.isGone = true
                    binding.elevation.isGone = true
                }
                else -> {
                    if (binding.bottomNavigationView.isGone) {
                        binding.root.postDelayed({
                            binding.bottomNavigationView.isGone = false
                            binding.elevation.isGone = false
                        }, 200)
                    } else {
                        binding.bottomNavigationView.isGone = false
                        binding.elevation.isGone = false
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root, null)
        _binding = null
    }
}
