package com.example.mundopeliculas.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.Movie

class RecommendationAdapter(private val onMovieClick: (Movie) -> Unit) :
    RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    private var movies: List<Movie> = emptyList()

    fun submitList(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search_recommendation,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie, onMovieClick)
    }

    override fun getItemCount(): Int = movies.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val poster: ImageView = itemView.findViewById(R.id.ivSearchPoster)
        private val title: TextView = itemView.findViewById(R.id.tvSearchMovieTitle)
        private val btnPlay: ImageView = itemView.findViewById(R.id.ivGoToDetails)

        fun bind(movie: Movie, onMovieClick: (Movie) -> Unit) {
            title.text = movie.title
            
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

            val action = { onMovieClick(movie) }
            itemView.setOnClickListener { action() }
            btnPlay.setOnClickListener { action() }
        }
    }
}
