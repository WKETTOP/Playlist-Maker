package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.search.ui.model.SearchViewState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchTrackViewModel>()

    private lateinit var trackAdapter: TrackAdapter

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var queryTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(
            onClick = viewModel::onTrackClicked,
            onLongItemClick = { }
        )

        binding.searchResult.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchResult.adapter = trackAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    renderViewState(state)
                    handlerEvents(state.uiEvent)
                }
            }
        }

        savedInstanceState?.getString(KEY_INPUT_TEXT)?.let {
            binding.trackInput.setText(it)
            viewModel.onQueryChanged(it)
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
        if (binding.trackInput.text.isEmpty() && !binding.trackInput.hasFocus() || binding.trackInput.hasFocus()) {
            viewModel.loadHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val inputText = binding.trackInput.text?.toString() ?: ""
        outState.putString(KEY_INPUT_TEXT, inputText)
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
        events.navigateToTrack?.let { track ->
            val bundle = bundleOf("TRACK" to track)
            findNavController().navigate(R.id.action_searchFragment_to_trackFragment, bundle)
            viewModel.clearEvents()
        }

        events.showToast?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            viewModel.clearEvents()
        }

        if (events.hideKeyboard) {
            hideKeyboard()
            viewModel.clearEvents()
        }
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
            hideSoftInputFromWindow(binding.trackInput.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        queryTextWatcher?.let {
            binding.trackInput.removeTextChangedListener(it)
        }
        _binding = null
    }

    companion object {
        const val KEY_INPUT_TEXT = "KEY_INPUT_TEXT"
    }
}
