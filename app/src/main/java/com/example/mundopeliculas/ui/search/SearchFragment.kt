package com.example.mundopeliculas.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.RetrofitClient
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var etSearchMovie: EditText
    private lateinit var ivSearchIcon: ImageView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var tvSearchStatus: TextView
    private lateinit var cgRecentSearches: ChipGroup
    private lateinit var layoutRecent: View

    private val recentSearches = mutableListOf<String>()
    private var searchJob: Job? = null
    private lateinit var movieAdapter: SearchMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_search,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearchMovie = view.findViewById(R.id.etSearchMovie)
        ivSearchIcon = view.findViewById(R.id.ivSearchIcon)
        val ivSearchFilter: ImageView = view.findViewById(R.id.ivSearchFilter)
        rvSearchResults = view.findViewById(R.id.rvSearchResults)
        tvSearchStatus = view.findViewById(R.id.tvSearchStatus)
        cgRecentSearches = view.findViewById(R.id.cgRecentSearches)
        layoutRecent = view.findViewById(R.id.layoutRecent)

        movieAdapter = SearchMovieAdapter()

        ivSearchFilter.setOnClickListener {
            if (!::movieAdapter.isInitialized || movieAdapter.getMovies().isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Primero realiza una búsqueda", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val popup = android.widget.PopupMenu(requireContext(), it)
            popup.menu.add("Mejor valoradas")
            popup.menu.add("Más recientes")
            popup.menu.add("A-Z")
            
            popup.setOnMenuItemClickListener { menuItem ->
                val currentList = movieAdapter.getMovies()
                val sortedList = when (menuItem.title) {
                    "Mejor valoradas" -> currentList.sortedByDescending { it.voteAverage }
                    "Más recientes" -> currentList.sortedByDescending { it.releaseDate }
                    "A-Z" -> currentList.sortedBy { it.title }
                    else -> currentList
                }
                movieAdapter.submitList(sortedList)
                true
            }
            popup.show()
        }

        rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        rvSearchResults.adapter = movieAdapter

        // Estado inicial: Mostrar Populares al entrar
        tvSearchStatus.text = "Películas Populares"
        loadRecommendations()

        // Buscar al presionar la lupa
        ivSearchIcon.setOnClickListener {
            performSearch()
        }

        // Buscar al presionar Enter en el teclado
        etSearchMovie.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Volver a recomendaciones si se limpia el buscador
        etSearchMovie.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    tvSearchStatus.text = getString(R.string.search_status_initial)
                    loadRecommendations()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch() {
        val query = etSearchMovie.text.toString().trim()
        if (query.isNotEmpty()) {
            searchMovies(query)
        } else {
            etSearchMovie.error = getString(R.string.search_enter_title)
        }
    }

    private fun searchMovies(query: String) {
        searchJob?.cancel()

        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            tvSearchStatus.text = getString(R.string.search_loading)
            movieAdapter.submitList(emptyList())

            try {
                val response = RetrofitClient.api.searchMovies(
                    apiKey = RetrofitClient.API_KEY,
                    query = query
                )

                val movies = response.results
                movieAdapter.submitList(movies)

                if (movies.isEmpty()) {
                    tvSearchStatus.text = getString(R.string.search_no_results)
                } else {
                    tvSearchStatus.text = getString(R.string.search_results_for, query)
                    updateRecentSearches(query)
                }

            } catch (e: Exception) {
                tvSearchStatus.text = getString(R.string.search_error)
            }
        }
    }

    private fun updateRecentSearches(query: String) {
        if (!recentSearches.contains(query)) {
            recentSearches.add(0, query)
            if (recentSearches.size > 5) {
                recentSearches.removeAt(recentSearches.lastIndex)
            }
            layoutRecent.visibility = View.VISIBLE
            refreshRecentChips()
        }
    }

    private fun refreshRecentChips() {
        cgRecentSearches.removeAllViews()
        recentSearches.forEach { text ->
            val chip = Chip(requireContext()).apply {
                this.text = text
                isClickable = true
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                setChipBackgroundColorResource(R.color.chip_bg_color)
                chipStrokeWidth = 0f
                setOnClickListener {
                    etSearchMovie.setText(text)
                    performSearch()
                }
            }
            cgRecentSearches.addView(chip)
        }
    }

    private fun loadRecommendations() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getPopularMovies(
                    apiKey = RetrofitClient.API_KEY
                )
                movieAdapter.submitList(response.results)
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }
}
