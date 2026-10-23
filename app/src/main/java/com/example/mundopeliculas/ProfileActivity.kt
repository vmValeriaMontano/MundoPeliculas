package com.example.mundopeliculas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mundopeliculas.data.FavoritesManager
import com.example.mundopeliculas.data.ListsManager
import com.example.mundopeliculas.data.ViewsManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = Firebase.auth
        db = Firebase.firestore

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvStatFavorites = findViewById<TextView>(R.id.tvStatFavorites)
        val tvStatViews = findViewById<TextView>(R.id.tvStatViews)
        val tvStatLists = findViewById<TextView>(R.id.tvStatLists)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val btnNotification = findViewById<android.widget.ImageView>(R.id.btnNotification)
        val btnMyActivity = findViewById<LinearLayout>(R.id.btnMyActivity)
        val btnMyFavorites = findViewById<LinearLayout>(R.id.btnMyFavorites)

        // Cargar contadores reales dinámicamente
        tvStatFavorites?.text = FavoritesManager.getFavorites(this).size.toString()
        tvStatViews?.text = ViewsManager.getViews(this).size.toString()
        tvStatLists?.text = ListsManager.getLists(this).size.toString()

        // Abrir Notificaciones / Actividad
        btnNotification?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("TARGET_FRAGMENT", "NOTIFICATIONS")
            })
            finish()
        }

        btnMyActivity?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("TARGET_FRAGMENT", "NOTIFICATIONS")
            })
            finish()
        }

        // Ir a Favoritos
        btnMyFavorites?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("TARGET_FRAGMENT", "FAVORITES")
            })
            finish()
        }

        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("TARGET_FRAGMENT", "HOME")
                    })
                    finish()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("TARGET_FRAGMENT", "SEARCH")
                    })
                    finish()
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("TARGET_FRAGMENT", "FAVORITES")
                    })
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            tvEmail.text = currentUser.email

            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fullName = document.getString("fullName")
                        tvName.text = fullName ?: "Usuario"
                    }
                }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}