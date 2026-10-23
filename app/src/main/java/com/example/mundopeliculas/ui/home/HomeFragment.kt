package com.example.mundopeliculas.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.Movie
import com.example.mundopeliculas.data.MovieAdapter
import com.example.mundopeliculas.data.RetrofitClient
import com.example.mundopeliculas.data.ViewsManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var layoutDots: LinearLayout

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvWelcome).text =
            "¡Bienvenido a Mundo Películas!"

        // 1. Configurar Carrusel de Tendencias (Grande)
        val vpTrending = view.findViewById<ViewPager2>(R.id.vpTrending)
        layoutDots = view.findViewById(R.id.layoutDots)

        // 2. Configurar Lista de Populares (Horizontal pequeño)
        val rvPopular = view.findViewById<RecyclerView>(R.id.rvPopular)
        rvPopular.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val popularAdapter = MovieAdapter(emptyList(), viewLifecycleOwner)
        rvPopular.adapter = popularAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Obtener Tendencias
                val trendingRes = RetrofitClient.api.getTrendingMovies(RetrofitClient.API_KEY)
                val movies = trendingRes.results.take(5) // Limitamos a 5 para el carrusel
                
                vpTrending.adapter = FeaturedMovieAdapter(movies) { movie ->
                    openTrailer(movie)
                }
                
                setupDots(movies.size)
                
                vpTrending.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        updateDots(position)
                    }
                })

                // Obtener Populares
                val popularRes = RetrofitClient.api.getPopularMovies(RetrofitClient.API_KEY)
                popularAdapter.updateMovies(popularRes.results)

            } catch (e: Exception) {
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "No se pudieron cargar las películas. Revisa Internet.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Configurar clic en "Ver todo" para ir a Búsqueda
        view.findViewById<TextView>(R.id.tvViewAllPopular).setOnClickListener {
            val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId = R.id.nav_search
        }

        // Configurar clic en Notificaciones
        view.findViewById<ImageView>(R.id.btnNotification).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.example.mundopeliculas.ui.notifications.NotificationsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupDots(count: Int) {
        layoutDots.removeAllViews()
        val dots = arrayOfNulls<ImageView>(count)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in 0 until count) {
            dots[i] = ImageView(requireContext())
            dots[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.dot_inactive
                )
            )
            layoutDots.addView(dots[i], params)
        }
        if (count > 0) updateDots(0)
    }

    private fun updateDots(position: Int) {
        val childCount = layoutDots.childCount
        for (i in 0 until childCount) {
            val imageView = layoutDots.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.dot_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.dot_inactive
                    )
                )
            }
        }
    }

    private fun openTrailer(movie: Movie) {
        // Registrar la película como "Vista" al ver su tráiler
        ViewsManager.addView(requireContext(), movie)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMovieVideos(movie.id, RetrofitClient.API_KEY)
                val video = response.results.firstOrNull {
                    it.site.equals("YouTube", ignoreCase = true) &&
                            (it.type.equals("Trailer", ignoreCase = true) || it.type.equals("Teaser", ignoreCase = true))
                }

                if (video != null) {
                    val url = "https://www.youtube.com/watch?v=${video.key}"
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(requireContext(), "Tráiler no disponible", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar tráiler", Toast.LENGTH_SHORT).show()
            }
        }
    }
}