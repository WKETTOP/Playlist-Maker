package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(), TrackClickListener {

    companion object {
        const val KEY_INPUT_TEXT = "KEY_INPUT_TEXT"
        const val TEXT_DEF = ""
    }

    private var textInputValue: String = TEXT_DEF
    private var lastSearchQuery: String? = null


    private val itunesService: ItunesApi by lazy {
        RetrofitClient.getApiService()
    }

    private lateinit var searchToolbar: Toolbar
    private lateinit var inputTextEdit: EditText
    private lateinit var clearButton: ImageView
    private lateinit var errorMessage: TextView
    private lateinit var errorImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var searchResult: RecyclerView
    private lateinit var trackSearchHistory: TrackSearchHistory
    private lateinit var findMessage: TextView
    private lateinit var clearTrackSearchHistory: Button
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val tracks = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_page)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchToolbar = findViewById(R.id.search_toolbar)
        inputTextEdit = findViewById(R.id.track_input)
        clearButton = findViewById(R.id.clear_text)
        errorMessage = findViewById(R.id.error_text)
        errorImage = findViewById(R.id.error_image)
        refreshButton = findViewById(R.id.refresh_button)
        searchResult = findViewById(R.id.search_result)
        findMessage = findViewById(R.id.find_text)
        clearTrackSearchHistory = findViewById(R.id.clear_track_history_button)

        sharedPreferences = getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        trackSearchHistory = TrackSearchHistory(sharedPreferences)

        trackAdapter = TrackAdapter(trackSearchHistory)
        trackAdapter.addObserver(this)

        trackAdapter.tracks = tracks

        searchResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchResult.adapter = trackAdapter

        showTrackSearchHistory()

        searchToolbar.setNavigationOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        clearButton.setOnClickListener {
            inputTextEdit.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputTextEdit.windowToken, 0)
            errorMessage.isVisible = false
            errorImage.isVisible = false
            refreshButton.isVisible = false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputValue = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
                if (s.isNullOrEmpty()) {
                    showTrackSearchHistory()
                } else {
                    findMessage.isVisible = false
                    clearTrackSearchHistory.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputTextEdit.addTextChangedListener(simpleTextWatcher)

        inputTextEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputTextEdit.text.isNotEmpty()) {
                    val query = inputTextEdit.text.toString()
                    performSearch(query)
                }
                true
            }
            false
        }

        inputTextEdit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputTextEdit.text.isEmpty()) {
                showTrackSearchHistory()
            }
        }

        refreshButton.setOnClickListener {
            lastSearchQuery?.let { query ->
                performSearch(query)
            }
        }

        clearTrackSearchHistory.setOnClickListener {
            trackSearchHistory.clearTrackSearchHistory()
            showTrackSearchHistory()
            findMessage.isVisible = false
            searchResult.isVisible = false
            clearTrackSearchHistory.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        trackAdapter.removeObserver(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_INPUT_TEXT, textInputValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textInputValue = savedInstanceState.getString(KEY_INPUT_TEXT, TEXT_DEF)
    }

    private fun showError(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            errorImage.isVisible = true
            errorMessage.isVisible = true
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            errorMessage.text = text
            when (additionalMessage.isEmpty()) {
                true -> {
                    errorImage.setImageResource(R.drawable.nothing_found_120)
                    refreshButton.isVisible = false
                }

                false -> {
                    errorImage.setImageResource(R.drawable.download_failed_120)
                    refreshButton.isVisible = true
                    Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            errorImage.isVisible = false
            errorMessage.isVisible = false
            refreshButton.isVisible = false
        }
    }

    private fun performSearch(query: String) {
        lastSearchQuery = query

        itunesService.search(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.isSuccessful) {
                    tracks.clear()
                    response.body()?.results?.let { results ->
                        if (results.isNotEmpty()) {
                            tracks.addAll(results)
                            trackAdapter.notifyDataSetChanged()
                            showError("", "")
                        } else {
                            showError(getString(R.string.nothing_found), "")
                        }
                    } ?: run {
                        showError(
                            getString(R.string.communication_problems),
                            response.code().toString()
                        )
                    }
                } else {
                    showError(
                        getString(R.string.communication_problems),
                        response.code().toString()
                    )
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showError(getString(R.string.communication_problems), t.message.toString())
            }
        })
    }

    private fun showTrackSearchHistory() {
        val history = trackSearchHistory.getTrackSearchHistory()

        if (history.isNotEmpty()) {
            trackAdapter.updateData(ArrayList(history))
            findMessage.setText(R.string.find_line)
            findMessage.isVisible = true
            clearTrackSearchHistory.isVisible = true
        } else {
            findMessage.isVisible = false
            clearTrackSearchHistory.isVisible = false
        }
    }

    override fun onTrackClicked(track: Track) {
        if (inputTextEdit.text.isNullOrEmpty()) {
            showTrackSearchHistory()
        }
    }
}
