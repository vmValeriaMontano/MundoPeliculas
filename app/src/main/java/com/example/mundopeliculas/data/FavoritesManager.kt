package com.example.mundopeliculas.data

import android.content.Context
import android.content.SharedPreferences
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
        }
    }

    fun removeFavorite(context: Context, movieId: Int) {
        val favorites = getFavorites(context)
        val removed = favorites.removeAll { it.id == movieId }
        if (removed) {
            saveFavorites(context, favorites)
        }
    }

    private fun saveFavorites(context: Context, favorites: List<Movie>) {
        val json = gson.toJson(favorites)
        getPrefs(context).edit().putString(KEY_FAVORITES, json).apply()
    }
}