package com.example.mundopeliculas.data

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        holder.rating.text = if (movie.voteAverage > 0) String.format("%.1f", movie.voteAverage) else context.getString(R.string.rating_na)
        holder.rank.text = (position + 1).toString()
        holder.genre.text = movie.genreIds?.firstOrNull()?.let { context.getString(getGenreResId(it)) } ?: context.getString(R.string.genre_default)

        // Calificación simple con estrellas
        val ratingValue = movie.voteAverage
        holder.stars.forEachIndexed { index, starView ->
            if (ratingValue >= (index + 1) * 2) {
                starView.setImageResource(android.R.drawable.btn_star_big_on)
                starView.setColorFilter(androidx.core.content.ContextCompat.getColor(context, R.color.gold), PorterDuff.Mode.SRC_IN)
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

        val posterUrl = movie.posterPath?.let { "${context.getString(R.string.tmdb_image_url_w342)}$it" }

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

    private fun getGenreResId(id: Int): Int {
        return when(id) {
            28 -> R.string.genre_action
            12 -> R.string.genre_adventure
            16 -> R.string.genre_animation
            35 -> R.string.genre_comedy
            80 -> R.string.genre_crime
            99 -> R.string.genre_documentary
            18 -> R.string.genre_drama
            10751 -> R.string.genre_family
            14 -> R.string.genre_fantasy
            36 -> R.string.genre_history
            27 -> R.string.genre_horror
            10402 -> R.string.genre_music
            9648 -> R.string.genre_mystery
            10749 -> R.string.genre_romance
            878 -> R.string.genre_science_fiction
            10770 -> R.string.genre_tv_movie
            53 -> R.string.genre_thriller
            10752 -> R.string.genre_war
            37 -> R.string.genre_western
            else -> R.string.genre_others
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}