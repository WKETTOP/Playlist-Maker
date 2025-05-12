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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.TrackActivity
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.model.SearchViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        const val KEY_INPUT_TEXT = "KEY_INPUT_TEXT"
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter

    private val viewModel by viewModel<SearchTrackViewModel>()

    private var queryTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trackAdapter = TrackAdapter(viewModel::onTrackClicked)

        binding.searchResult.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.searchResult.adapter = trackAdapter

        viewModel.state.observe(this) { state ->
            renderViewState(state)
            handlerEvents(state.uiEvent)
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        queryTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onQueryChanged(s?.toString() ?: "")
                binding.clearText.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        binding.trackInput.addTextChangedListener(queryTextWatcher)

        binding.clearText.setOnClickListener {
            viewModel.clearSearch()
        }

        binding.trackInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.trackInput.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        binding.trackInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchDebounce(binding.trackInput.text.toString())
        }

        binding.clearTrackHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.trackInput.text.isEmpty() && binding.trackInput.hasFocus()) {
            viewModel.loadHistory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        queryTextWatcher?.let {
            binding.trackInput.removeTextChangedListener(it)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, binding.trackInput.text.toString())
    }

    private fun renderViewState(state: SearchViewState) {
        val searchState = state.searchState
        if (binding.trackInput.text.toString() != state.searchQuery) {
            binding.trackInput.setText(state.searchQuery)
            binding.trackInput.setSelection(state.searchQuery.length)
        }

        binding.clearText.isVisible = state.searchQuery.isNotEmpty()

        binding.searchProgress.isVisible = searchState is SearchState.Loading
        binding.errorImage.isVisible = searchState is SearchState.Error || searchState is SearchState.Empty
        binding.errorText.isVisible = searchState is SearchState.Error || searchState is SearchState.Empty
        binding.refreshButton.isVisible = searchState is SearchState.Error
        binding.findText.isVisible = searchState is SearchState.History && searchState.tracks.isNotEmpty()
        binding.clearTrackHistoryButton.isVisible = searchState is SearchState.History && searchState.tracks.isNotEmpty()

        binding.searchResult.isVisible = when {
            searchState is SearchState.Content -> true
            searchState is SearchState.History && searchState.tracks.isNotEmpty() -> true
            else -> false
        }

        when (searchState) {
            is SearchState.Content -> {
                trackAdapter.updateData(searchState.tracks)
            }

            is SearchState.History -> {
                if (searchState.tracks.isNotEmpty()) {
                    binding.findText.setText(R.string.find_line)
                    trackAdapter.updateData(searchState.tracks)
                } else {
                    trackAdapter.updateData(emptyList())
                }
            }

            is SearchState.Error -> {
                binding.searchProgress.isVisible = false
                binding.errorText.setText(R.string.communication_problems)
                binding.errorImage.setImageResource(R.drawable.download_failed_120)
                binding.refreshButton.isVisible = true
                trackAdapter.updateData(emptyList())
            }

            is SearchState.Empty -> {
                binding.searchProgress.isVisible = false
                binding.errorText.setText(R.string.nothing_found)
                binding.errorImage.setImageResource(R.drawable.nothing_found_120)
                trackAdapter.updateData(emptyList())
            }

            is SearchState.Loading -> {
                trackAdapter.updateData(emptyList())
            }
        }
    }

    private fun handlerEvents(events: SearchViewState.UiEvents) {
        events.navigateToTrack?.let {
            startActivity(Intent(this, TrackActivity::class.java).apply {
                putExtra("TRACK", it)
            })
            viewModel.clearEvents()
        }

        events.showToast?.let {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            viewModel.clearEvents()
        }

        if (events.hideKeyboard) {
            hideKeyboard()
            viewModel.clearEvents()
        }
    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
            hideSoftInputFromWindow(binding.trackInput.windowToken, 0)
        }
    }
}
