package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.TrackActivity
import com.example.playlistmaker.search.domain.model.Track

class SearchActivity : AppCompatActivity() {

    companion object {
        const val KEY_INPUT_TEXT = "KEY_INPUT_TEXT"
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchTrackViewModel
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_page)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, SearchTrackViewModel.getViewModelFactory())[SearchTrackViewModel::class.java]

        trackAdapter = TrackAdapter(viewModel::onTrackClicked)

        binding.searchResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.searchResult.adapter = trackAdapter

        val savedText = savedInstanceState?.getString(KEY_INPUT_TEXT)
        if (savedText != null) {
            viewModel.setSearchQuery(savedText)
        }

        viewModel.observeSearchQuery().observe(this) { query ->
            if (query != binding.trackInput.text.toString()) {
                binding.trackInput.setText(query)
            }
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.trackInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(s?.toString() ?: "")
                binding.clearText.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s.toString())
            }
        })

        binding.clearText.setOnClickListener {
            binding.trackInput.setText("")
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.trackInput.windowToken, 0)
            binding.errorText.isVisible = false
            binding.errorImage.isVisible = false
            binding.refreshButton.isVisible = false
            viewModel.loadHistory()
            showHistory(viewModel.observeHistory().value ?: emptyList())
        }

        binding.trackInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.trackInput.text.isEmpty()) {
                viewModel.loadHistory()
                showHistory(viewModel.observeHistory().value ?: emptyList())
            }
        }

        binding.trackInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.trackInput.text.isNotEmpty()) {
                    val query = binding.trackInput.text.toString()
                    viewModel.searchDebounce(query)
                }
                true
            }
            false
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchDebounce(binding.trackInput.text.toString())
        }

        binding.clearTrackHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.searchResult.isVisible = false
        }

        viewModel.observeState().observe(this) { state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state.tracks)
                is SearchState.Error -> showError()
                is SearchState.Empty -> showEmpty()
            }
        }

        viewModel.observeHistory().observe(this) { history ->
            showHistory(history)
        }

        viewModel.observeNavigateToTrack().observe(this) { track ->
            startActivity(Intent(this, TrackActivity::class.java).apply {
                putExtra("TRACK", track)
            })
        }

        viewModel.observerShowToast().observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.trackInput.text.isEmpty() && binding.trackInput.hasFocus()) {
            viewModel.loadHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, viewModel.observeSearchQuery().value ?: "")
    }

    private fun showError() {
        binding.searchProgress.isVisible = false
        binding.errorText.isVisible = true
        binding.errorImage.isVisible = true
        binding.errorText.setText(R.string.communication_problems)
        binding.errorImage.setImageResource(R.drawable.download_failed_120)
        binding.refreshButton.isVisible = true
    }

    private fun showHistory(history: List<Track>) {
        if (history.isNotEmpty()) {
            trackAdapter.updateData(history)
            binding.findText.setText(R.string.find_line)
            binding.findText.isVisible = true
            binding.clearTrackHistoryButton.isVisible = true
            binding.searchResult.isVisible = true
        } else {
            binding.findText.isVisible = false
            binding.clearTrackHistoryButton.isVisible = false
        }
    }

    private fun clearErrorState() {
        binding.errorImage.isVisible = false
        binding.errorText.isVisible = false
        binding.refreshButton.isVisible = false
    }

    private fun showLoading() {
        binding.searchProgress.isVisible = true
        clearErrorState()
        binding.refreshButton.isVisible = false
        binding.findText.isVisible = false
        binding.clearTrackHistoryButton.isVisible = false
        binding.searchResult.isVisible = false
    }

    private fun showContent(track: List<Track>) {
        binding.searchProgress.isVisible = false
        clearErrorState()
        binding.refreshButton.isVisible = false
        binding.findText.isVisible = false
        binding.searchResult.isVisible = true
        trackAdapter.updateData(track)
    }

    private fun showEmpty() {
        binding.searchProgress.isVisible = false
        binding.errorText.isVisible = true
        binding.errorImage.isVisible = true
        binding.errorText.setText(R.string.nothing_found)
        binding.errorImage.setImageResource(R.drawable.nothing_found_120)
        binding.refreshButton.isVisible = true
    }
}
