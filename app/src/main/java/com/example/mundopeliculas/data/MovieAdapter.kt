package com.example.mundopeliculas.data

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mundopeliculas.R

class MovieAdapter(
    private var movies: List<Movie>,
    @Suppress("UNUSED_PARAMETER") private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.ivPoster)
        val title: TextView = view.findViewById(R.id.tvMovieTitleLabel)
        val rating: TextView = view.findViewById(R.id.tvMovieRating)
        val rank: TextView = view.findViewById(R.id.tvMovieRank)
        val genre: TextView = view.findViewById(R.id.tvMovieGenre)
        val progressFill: View = view.findViewById(R.id.viewMovieProgressFill)
        
        val stars = listOf<ImageView>(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val context = holder.itemView.context

        holder.title.text = movie.title
        holder.rating.text = if (movie.voteAverage > 0) String.format("%.1f", movie.voteAverage) else "N/A"
        holder.rank.text = (position + 1).toString()
        holder.genre.text = movie.genreIds?.firstOrNull()?.let { getGenreName(it) } ?: "Cine"

        // Calificación simple con estrellas
        val ratingValue = movie.voteAverage
        holder.stars.forEachIndexed { index, starView ->
            if (ratingValue >= (index + 1) * 2) {
                starView.setImageResource(android.R.drawable.btn_star_big_on)
                starView.setColorFilter(Color.parseColor("#FFC107"), PorterDuff.Mode.SRC_IN)
            } else {
                starView.setImageResource(android.R.drawable.btn_star_big_off)
                starView.clearColorFilter()
            }
        }

        // Simulación de progreso de visualización
        val density = context.resources.displayMetrics.density
        val progressPercent = (movie.voteAverage * 10).toInt().coerceIn(20, 100)
        val params = holder.progressFill.layoutParams
        params.width = (progressPercent * density).toInt()
        holder.progressFill.layoutParams = params

        val posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }

        Glide.with(holder.poster)
            .load(posterUrl)
            .placeholder(android.R.color.darker_gray)
            .centerCrop()
            .into(holder.poster)

        //Detalles de la peli
        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(context, com.example.mundopeliculas.ui.details.DetailActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.id)
                putExtra("MOVIE_TITLE", movie.title)
                putExtra("MOVIE_OVERVIEW", movie.overview)
                putExtra("MOVIE_POSTER", movie.posterPath)
                putExtra("MOVIE_BACKDROP", movie.backdropPath)
                putExtra("MOVIE_RATING", movie.voteAverage)
                putExtra("MOVIE_RELEASE_DATE", movie.releaseDate)
            }
            context.startActivity(intent)
        }
    }

    private fun getGenreName(id: Int): String {
        return when(id) {
            28 -> "Acción"
            12 -> "Aventura"
            16 -> "Animación"
            35 -> "Comedia"
            80 -> "Crimen"
            99 -> "Documental"
            18 -> "Drama"
            10751 -> "Familia"
            14 -> "Fantasía"
            36 -> "Historia"
            27 -> "Terror"
            10402 -> "Música"
            9648 -> "Misterio"
            10749 -> "Romance"
            878 -> "Ciencia ficción"
            10770 -> "Película de TV"
            53 -> "Suspense"
            10752 -> "Bélica"
            37 -> "Western"
            else -> "Otros"
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}