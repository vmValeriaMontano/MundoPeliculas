package com.example.mundopeliculas.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object ViewsManager {
    private const val PREFS_NAME = "views_prefs"
    private const val KEY_VIEWS = "saved_views"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getViews(context: Context): List<Movie> {
        val json = getPrefs(context).getString(KEY_VIEWS, null) ?: return emptyList()
        val type = object : TypeToken<List<Movie>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addView(context: Context, movie: Movie) {
        val views = getViews(context).toMutableList()
        // Evitar duplicados si ya fue vista
        if (!views.any { it.id == movie.id }) {
            views.add(0, movie)
            val json = gson.toJson(views)
            getPrefs(context).edit().putString(KEY_VIEWS, json).apply()

            // Sincronizar con Firestore en segundo plano
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                val movieMap = hashMapOf(
                    "id" to movie.id,
                    "title" to movie.title,
                    "overview" to movie.overview,
                    "posterPath" to movie.posterPath,
                    "backdropPath" to movie.backdropPath,
                    "voteAverage" to movie.voteAverage,
                    "releaseDate" to movie.releaseDate
                )
                db.collection("users").document(userId).collection("views")
                    .document(movie.id.toString()).set(movieMap)
            }
        }
    }
}