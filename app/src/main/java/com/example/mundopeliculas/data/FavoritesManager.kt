package com.example.mundopeliculas.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesManager {
    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorite_movies"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFavorites(context: Context): MutableList<Movie> {
        val json = getPrefs(context).getString(KEY_FAVORITES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Movie>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isFavorite(context: Context, movieId: Int): Boolean {
        return getFavorites(context).any { it.id == movieId }
    }

    fun addFavorite(context: Context, movie: Movie) {
        val favorites = getFavorites(context)
        if (!favorites.any { it.id == movie.id }) {
            favorites.add(movie)
            saveFavorites(context, favorites)

            // Sincronizar con Firestore (La Nube)
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                val movieMap = hashMapOf(
                    "id" to movie.id,
                    "title" to movie.title,
                    "overview" to movie.overview,
                    "posterPath" to movie.posterPath,
                    "voteAverage" to movie.voteAverage
                )
                db.collection("users").document(userId).collection("favorites")
                    .document(movie.id.toString()).set(movieMap)
            }
        }
    }

    fun removeFavorite(context: Context, movieId: Int) {
        val favorites = getFavorites(context)
        val removed = favorites.removeAll { it.id == movieId }
        if (removed) {
            saveFavorites(context, favorites)

            // Borrar de Firestore (La Nube)
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(userId).collection("favorites")
                    .document(movieId.toString()).delete()
            }
        }
    }

    private fun saveFavorites(context: Context, favorites: List<Movie>) {
        val json = gson.toJson(favorites)
        getPrefs(context).edit().putString(KEY_FAVORITES, json).apply()
    }
}