package com.example.mundopeliculas.ui.list

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mundopeliculas.R
import com.example.mundopeliculas.data.ListsManager
import com.example.mundopeliculas.data.MovieAdapter

class ListActivity : AppCompatActivity() {

    private lateinit var rvListMovies: RecyclerView
    private lateinit var tvEmptyList: TextView
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val btnBack = findViewById<ImageButton>(R.id.btnBackList)
        rvListMovies = findViewById(R.id.rvListMovies)
        tvEmptyList = findViewById(R.id.tvEmptyList)

        btnBack.setOnClickListener { finish() }

        // (una al lado de la otra)
        rvListMovies.layoutManager = GridLayoutManager(this, 2)

        loadListMovies()
    }

    override fun onResume() {
        super.onResume()
        loadListMovies()
    }

    private fun loadListMovies() {
        val movies = ListsManager.getLists(this)

        if (movies.isEmpty()) {
            tvEmptyList.visibility = View.VISIBLE
            rvListMovies.visibility = View.GONE
        } else {
            tvEmptyList.visibility = View.GONE
            rvListMovies.visibility = View.VISIBLE

            adapter = MovieAdapter(movies, this)
            rvListMovies.adapter = adapter
        }
    }
}