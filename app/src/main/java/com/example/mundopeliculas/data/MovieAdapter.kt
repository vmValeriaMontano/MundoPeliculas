package com.example.mundopeliculas.data

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val context = holder.itemView.context

        holder.title.text = movie.title
        holder.rating.text = "%.1f".format(movie.voteAverage)
        holder.rank.text = (position + 1).toString()
        
        // Mocking genre para el diseño premium
        holder.genre.text = when(position % 3) {
            0 -> "Ciencia Ficción"
            1 -> "Acción"
            else -> "Drama"
        }

        // Simulación de progreso de visualización
        val density = context.resources.displayMetrics.density
        val progressWidth = (40..100).random()
        val params = holder.progressFill.layoutParams
        params.width = (progressWidth * density).toInt()
        holder.progressFill.layoutParams = params

        val posterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w342$it" }

        Glide.with(holder.poster)
            .load(posterUrl)
            .placeholder(android.R.color.darker_gray)
            .centerCrop()
            .into(holder.poster)

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "Detalles de ${movie.title} (Próximamente)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}