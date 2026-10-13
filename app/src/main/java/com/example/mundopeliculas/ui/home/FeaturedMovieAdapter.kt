package com.example.mundopeliculas.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.Movie

class FeaturedMovieAdapter(
    private val movies: List<Movie>,
    private val onTrailerClick: (Movie) -> Unit
) : RecyclerView.Adapter<FeaturedMovieAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val backdrop: ImageView = view.findViewById(R.id.ivBackdrop)
        val title: TextView = view.findViewById(R.id.tvFeaturedTitle)
        val rating: TextView = view.findViewById(R.id.tvRatingFeatured)
        val btnTrailer: View = view.findViewById(R.id.btnFeaturedTrailer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_featured_movie, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.title.text = movie.title
        holder.rating.text = "%.1f".format(movie.voteAverage)
        
        val backdropUrl = "https://image.tmdb.org/t/p/w780${movie.backdropPath}"
        
        Glide.with(holder.backdrop)
            .load(backdropUrl)
            .centerCrop()
            .placeholder(android.R.color.darker_gray)
            .into(holder.backdrop)

        holder.btnTrailer.setOnClickListener { onTrailerClick(movie) }
    }

    override fun getItemCount() = movies.size
}
