package com.example.mundopeliculas.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object ListsManager {
    private const val PREFS_NAME = "lists_prefs"
    private const val KEY_LISTS = "saved_lists"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getLists(context: Context): List<Movie> {
        val json = getPrefs(context).getString(KEY_LISTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Movie>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isinList(context: Context, movieId: Int): Boolean {
        return getLists(context).any { it.id == movieId }
    }

    fun addList(context: Context, movie: Movie) {
        val lists = getLists(context).toMutableList()
        if (!lists.any { it.id == movie.id }) {
            lists.add(0, movie)
            saveLists(context, lists)

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
                db.collection("users").document(userId).collection("lists")
                    .document(movie.id.toString()).set(movieMap)
            }
        }
    }

    fun removeList(context: Context, movieId: Int) {
        val lists = getLists(context).toMutableList()
        val removed = lists.removeAll { it.id == movieId }
        if (removed) {
            saveLists(context, lists)

            // Borrar de Firestore
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(userId).collection("lists")
                    .document(movieId.toString()).delete()
            }
        }
    }

    private fun saveLists(context: Context, lists: List<Movie>) {
        val json = gson.toJson(lists)
        getPrefs(context).edit().putString(KEY_LISTS, json).apply()
    }
}