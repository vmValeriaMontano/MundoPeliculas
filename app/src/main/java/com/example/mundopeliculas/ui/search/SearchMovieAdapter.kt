package com.example.mundopeliculas.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.FavoritesManager
import com.example.mundopeliculas.data.Movie
import com.example.mundopeliculas.ui.notifications.NotificationsManager

class SearchMovieAdapter :
    RecyclerView.Adapter<SearchMovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = emptyList()
    var onFavoriteChanged: (() -> Unit)? = null

    fun submitList(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    fun getMovies(): List<Movie> = movies

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search_movie,
            parent,
            false
        )
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val context = holder.itemView.context
        val isFav = FavoritesManager.isFavorite(context, movie.id)
        
        holder.bind(movie, isFav) { updatedFav ->
            if (updatedFav) {
                FavoritesManager.addFavorite(context, movie)
                NotificationsManager.addNotification(
                    context,
                    context.getString(R.string.notification_fav_added_title),
                    context.getString(R.string.notification_fav_added_msg, movie.title),
                    "reminder"
                )
                Toast.makeText(context, context.getString(R.string.fav_added_toast, movie.title), Toast.LENGTH_SHORT).show()
            } else {
                FavoritesManager.removeFavorite(context, movie.id)
                NotificationsManager.addNotification(
                    context,
                    context.getString(R.string.notification_fav_removed_title),
                    context.getString(R.string.notification_fav_removed_msg, movie.title),
                    "reminder"
                )
                Toast.makeText(context, context.getString(R.string.fav_removed_toast, movie.title), Toast.LENGTH_SHORT).show()
            }
            onFavoriteChanged?.invoke()
        }
    }

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val poster: ImageView = itemView.findViewById(R.id.ivSearchPoster)
        private val title: TextView = itemView.findViewById(R.id.tvSearchMovieTitle)
        private val meta: TextView = itemView.findViewById(R.id.tvMovieMeta)
        private val rating: TextView = itemView.findViewById(R.id.tvMovieRating)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
        private val btnPlay: ImageView = itemView.findViewById(R.id.ivGoToDetails)

        fun bind(movie: Movie, isFavorite: Boolean, onFavoriteToggle: (Boolean) -> Unit) {
            title.text = movie.title
            rating.text = String.format("%.1f", movie.voteAverage)
            
            val year = movie.releaseDate?.split("-")?.firstOrNull() ?: "N/A"
            meta.text = "$year • Película"

            val posterUrl = movie.backdropPath?.let {
                "https://image.tmdb.org/t/p/w780$it"
            } ?: movie.posterPath?.let {
                "https://image.tmdb.org/t/p/w342$it"
            }

            Glide.with(itemView)
                .load(posterUrl)
                .placeholder(R.color.search_bar_bg)
                .centerCrop()
                .into(poster)

            var currentFavoriteState = isFavorite
            updateFavoriteIcon(currentFavoriteState)

            ivFavorite.setOnClickListener {
                currentFavoriteState = !currentFavoriteState
                updateFavoriteIcon(currentFavoriteState)
                onFavoriteToggle(currentFavoriteState)
            }

            val playAction = {
                Toast.makeText(itemView.context, "Reproduciendo: ${movie.title}", Toast.LENGTH_SHORT).show()
            }

            btnPlay.setOnClickListener { playAction() }
            itemView.setOnClickListener { playAction() }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_heart_filled)
                ivFavorite.setColorFilter(ContextCompat.getColor(itemView.context, R.color.heart_red))
            } else {
                ivFavorite.setImageResource(R.drawable.ic_heart_empty)
                ivFavorite.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white))
            }
        }
    }
}
