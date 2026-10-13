package com.example.mundopeliculas.ui.notifications

data class AppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val type: String // "new_movie", "reminder", "promo"
)