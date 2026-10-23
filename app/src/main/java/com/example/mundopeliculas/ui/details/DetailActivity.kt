package com.example.mundopeliculas.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.FavoritesManager
import com.example.mundopeliculas.data.ListsManager
import com.example.mundopeliculas.data.Movie
import com.example.mundopeliculas.data.RetrofitClient
import com.example.mundopeliculas.data.ViewsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private var movieId: Int = 0
    private var movieTitle: String = ""
    private var movieOverview: String = ""
    private var moviePoster: String? = null
    private var movieBackdrop: String? = null
    private var movieRating: Double = 0.0
    private var movieReleaseDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Recibir datos del Intent
        movieId = intent.getIntExtra("MOVIE_ID", 0)
        movieTitle = intent.getStringExtra("MOVIE_TITLE") ?: ""
        movieOverview = intent.getStringExtra("MOVIE_OVERVIEW") ?: ""
        moviePoster = intent.getStringExtra("MOVIE_POSTER")
        movieBackdrop = intent.getStringExtra("MOVIE_BACKDROP")
        movieRating = intent.getDoubleExtra("MOVIE_RATING", 0.0)
        movieReleaseDate = intent.getStringExtra("MOVIE_RELEASE_DATE")

        val ivBackdrop = findViewById<ImageView>(R.id.ivDetailBackdrop)
        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvRating = findViewById<TextView>(R.id.tvDetailRating)
        val tvYear = findViewById<TextView>(R.id.tvDetailYear)
        val tvDate = findViewById<TextView>(R.id.tvDetailDate)
        val tvOverview = findViewById<TextView>(R.id.tvDetailOverview)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnFavorite = findViewById<ToggleButton>(R.id.btnFavorite)
        val btnList = findViewById<ToggleButton>(R.id.btnList)
        val btnWatchTrailer = findViewById<Button>(R.id.btnWatchTrailer)

        tvTitle.text = movieTitle
        tvRating.text = "★ ${if (movieRating > 0) String.format("%.1f", movieRating) else "N/A"}"

        val year = movieReleaseDate?.takeIf { it.length >= 4 }?.substring(0, 4) ?: "N/A"
        tvYear.text = year
        tvDate.text = "Estreno: ${movieReleaseDate ?: "N/A"}"
        tvOverview.text = movieOverview.ifEmpty { "Sin sinopsis disponible." }

        val imageUrl = movieBackdrop ?: moviePoster
        val fullImageUrl = imageUrl?.let { "https://image.tmdb.org/t/p/w780$it" }

        Glide.with(this)
            .load(fullImageUrl)
            .placeholder(android.R.color.darker_gray)
            .centerCrop()
            .into(ivBackdrop)

        btnBack.setOnClickListener { finish() }

        val currentMovie = Movie(
            id = movieId,
            title = movieTitle,
            overview = movieOverview,
            posterPath = moviePoster,
            backdropPath = movieBackdrop,
            voteAverage = movieRating,
            releaseDate = movieReleaseDate,
            genreIds = null
        )

        // Estado inicial de Favoritos y Listas
        btnFavorite.isChecked = FavoritesManager.isFavorite(this, movieId)
        btnList.isChecked = ListsManager.isinList(this, movieId)

        // Lógica de Favoritos
        btnFavorite.setOnCheckedChangeListener { _, isChecked ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val db = FirebaseFirestore.getInstance()

            if (isChecked) {
                FavoritesManager.addFavorite(this, currentMovie)
                Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                if (userId != null) {
                    val movieMap = hashMapOf(
                        "id" to movieId, "title" to movieTitle, "overview" to movieOverview,
                        "posterPath" to moviePoster, "backdropPath" to movieBackdrop,
                        "voteAverage" to movieRating, "releaseDate" to movieReleaseDate
                    )
                    db.collection("users").document(userId).collection("favorites").document(movieId.toString()).set(movieMap)
                }
            } else {
                FavoritesManager.removeFavorite(this, movieId)
                Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                if (userId != null) {
                    db.collection("users").document(userId).collection("favorites").document(movieId.toString()).delete()
                }
            }
        }

        // Lógica de Listas [+]
        btnList.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ListsManager.addList(this, currentMovie)
                Toast.makeText(this, "Añadido a mi lista", Toast.LENGTH_SHORT).show()
            } else {
                ListsManager.removeList(this, movieId)
                Toast.makeText(this, "Eliminado de mi lista", Toast.LENGTH_SHORT).show()
            }
        }

        // Lógica del botón Ver Tráiler (Registra la vista)
        btnWatchTrailer.setOnClickListener {
            ViewsManager.addView(this, currentMovie)
            openTrailer()
        }
    }

    private fun openTrailer() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMovieVideos(movieId, RetrofitClient.API_KEY)
                val video = response.results.firstOrNull {
                    it.site.equals("YouTube", ignoreCase = true) &&
                            (it.type.equals("Trailer", ignoreCase = true) || it.type.equals("Teaser", ignoreCase = true))
                }

                if (video != null) {
                    val url = "https://www.youtube.com/watch?v=${video.key}"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(this@DetailActivity, "Tráiler no disponible", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Error al cargar tráiler", Toast.LENGTH_SHORT).show()
            }
        }
    }
}