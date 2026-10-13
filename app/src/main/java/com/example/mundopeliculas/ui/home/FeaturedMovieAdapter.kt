package com.example.mundopeliculas.ui.home

import android.graphics.Color
import android.graphics.PorterDuff
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
        
        val stars = listOf<ImageView>(
            view.findViewById(R.id.fStar1),
            view.findViewById(R.id.fStar2),
            view.findViewById(R.id.fStar3),
            view.findViewById(R.id.fStar4),
            view.findViewById(R.id.fStar5)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_featured_movie, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.title.text = movie.title
        
        // Lógica de Calificación Precisa y Estrellas Premium (Anti-Quemado)
        val ratingStars = movie.voteAverage / 2.0
        val goldColor = Color.parseColor("#FFC107") // Oro Premium
        val darkGray = Color.parseColor("#424242")  // Gris para vacías

        holder.stars.forEachIndexed { index, starView ->
            val starLevel = index + 1
            when {
                ratingStars >= starLevel - 0.1 -> { // 90% de la estrella -> Llena
                    starView.setImageResource(android.R.drawable.btn_star_big_on)
                    starView.setColorFilter(goldColor, PorterDuff.Mode.SRC_IN)
                    starView.alpha = 1.0f
                }
                ratingStars >= starLevel - 0.6 -> { // 40% de la estrella -> Media
                    starView.setImageResource(android.R.drawable.btn_star_big_on)
                    starView.setColorFilter(goldColor, PorterDuff.Mode.SRC_IN)
                    starView.alpha = 0.5f // Brillo suave para media estrella
                }
                else -> { // Vacía
                    starView.setImageResource(android.R.drawable.btn_star_big_off)
                    starView.setColorFilter(darkGray, PorterDuff.Mode.SRC_IN)
                    starView.alpha = 1.0f
                }
            }
        }
        holder.rating.text = if (movie.voteAverage > 0) String.format("%.1f", movie.voteAverage) else ""
        
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
