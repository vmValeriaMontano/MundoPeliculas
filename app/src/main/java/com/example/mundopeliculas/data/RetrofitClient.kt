package com.example.mundopeliculas.data

import com.example.mundopeliculas.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = BuildConfig.TMDB_API_KEY

    val instance: MovieApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MovieApi::class.java)
    }

    val api: MovieApi = instance
}