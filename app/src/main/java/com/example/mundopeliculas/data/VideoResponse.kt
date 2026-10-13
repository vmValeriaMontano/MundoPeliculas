package com.example.mundopeliculas.data

data class VideoResponse(
    val results: List<MovieVideo> = emptyList()
)

data class MovieVideo(
    val key: String,
    val site: String,
    val type: String,
    val official: Boolean = false
)