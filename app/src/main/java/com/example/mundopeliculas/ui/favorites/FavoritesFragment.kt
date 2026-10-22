package com.example.mundopeliculas.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.FavoritesManager
import com.example.mundopeliculas.ui.search.SearchMovieAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesFragment : Fragment() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var tvEmptyFavorites: TextView
    private lateinit var movieAdapter: SearchMovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvFavorites = view.findViewById(R.id.rvFavorites)
        tvEmptyFavorites = view.findViewById(R.id.tvEmptyFavorites)

        movieAdapter = SearchMovieAdapter()
        rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        rvFavorites.adapter = movieAdapter

        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        // 1. LO QUE YA FUNCIONA: Carga rápida y estable desde el almacenamiento local
        val favorites = FavoritesManager.getFavorites(requireContext())
        if (favorites.isEmpty()) {
            tvEmptyFavorites.visibility = View.VISIBLE
            rvFavorites.visibility = View.GONE
        } else {
            tvEmptyFavorites.visibility = View.GONE
            rvFavorites.visibility = View.VISIBLE
            movieAdapter.submitList(favorites)
        }

        // 2. NUEVO: Sincronización automática con Firestore en segundo plano
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null && favorites.isNotEmpty()) {
            val db = FirebaseFirestore.getInstance()
            val favCollection = db.collection("users").document(userId).collection("favorites")

            for (movie in favorites) {
                val movieMap = hashMapOf(
                    "id" to movie.id,
                    "title" to movie.title,
                    "overview" to movie.overview,
                    "posterPath" to movie.posterPath,
                    "backdropPath" to movie.backdropPath,
                    "voteAverage" to movie.voteAverage,
                    "releaseDate" to movie.releaseDate
                )
                favCollection.document(movie.id.toString()).set(movieMap)
            }
        }
    }
}