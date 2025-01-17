package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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

class SearchActivity : AppCompatActivity() {

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
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

    private val tracks = ArrayList<Track>()
    private var searchHistory = mutableListOf<String>()

    private val trackAdapter = TrackAdapter()

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
        historyRecyclerView = findViewById(R.id.search_history)

        trackAdapter.tracks = tracks

        historyAdapter = HistoryAdapter(searchHistory) { query ->
            inputTextEdit.setText(query)
            performSearch(query)
        }

        searchResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchResult.adapter = trackAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyRecyclerView.adapter = historyAdapter

        searchToolbar.setNavigationOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        clearButton.setOnClickListener {
            inputTextEdit.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputTextEdit.windowToken, 0)
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
                    hideResultsAndShowHistory()
                } else {
                    searchResult.visibility = View.VISIBLE
                    historyRecyclerView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputTextEdit.addTextChangedListener(simpleTextWatcher)

        inputTextEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputTextEdit.text.isNotEmpty()) {
                    performSearch(inputTextEdit.text.toString())
                }
                true
            }
            false
        }

        refreshButton.setOnClickListener {
            lastSearchQuery?.let { query ->
                performSearch(query)
            }
        }
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
            errorImage.visibility = View.VISIBLE
            errorMessage.visibility = View.VISIBLE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            errorMessage.text = text
            when (additionalMessage.isEmpty()) {
                true -> {
                    errorImage.setImageResource(R.drawable.nothing_found_120)
                    refreshButton.visibility = View.GONE
                }

                false -> {
                    errorImage.setImageResource(R.drawable.download_failed_120)
                    refreshButton.visibility = View.VISIBLE
                    Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            errorImage.visibility = View.GONE
            errorMessage.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }

    private fun performSearch(query: String) {
        lastSearchQuery = query
        searchHistory.add(query)
        itunesService.search(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
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
                        showError(getString(R.string.communication_problems), response.code().toString())
                    }
                } else {
                    showError(getString(R.string.communication_problems), response.code().toString())
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showError(getString(R.string.communication_problems), t.message.toString())
            }
        })
    }

    private fun hideResultsAndShowHistory() {
        searchResult.visibility = View.GONE
        if (searchHistory.isNotEmpty()) {
            historyRecyclerView.visibility = View.VISIBLE
            historyAdapter.notifyDataSetChanged()
        } else {
            historyRecyclerView.visibility = View.GONE
        }
    }
}
