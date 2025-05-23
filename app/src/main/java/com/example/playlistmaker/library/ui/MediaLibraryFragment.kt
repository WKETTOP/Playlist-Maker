package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryFragment : Fragment() {

    private var _binding: FragmentMediaLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mediaLibraryViewPager.adapter = MediaLibraryViewPagerAdapter(
            fragmentManager = childFragmentManager,
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tabLayoutMediator.detach()
    }
}