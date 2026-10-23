package com.example.mundopeliculas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val btnNotification = findViewById<android.widget.ImageView>(R.id.btnNotification)
        val btnMyFavorites = findViewById<LinearLayout>(R.id.btnMyFavorites) // Botón de Mis Favoritos en la lista

        btnNotification.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("TARGET_FRAGMENT", "NOTIFICATIONS")
            })
            finish()
        }

        // Al presionar la opción "Mis favoritos" en la lista del perfil, nos lleva a la pestaña Favoritos
        btnMyFavorites?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("TARGET_FRAGMENT", "FAVORITES")
            })
            finish()
        }

        // Marcar la opción "Perfil" como seleccionada en esta Activity
        bottomNav.selectedItemId = R.id.nav_profile

        // Configurar la navegación con Intent entre Activities
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
                R.id.nav_favorites -> { // AHORA SÍ: Redirige a la pantalla de Favoritos
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("TARGET_FRAGMENT", "FAVORITES")
                    })
                    finish()
                    true
                }
                R.id.nav_profile -> true // Ya estamos en Perfil
                else -> false
            }
        }

        // Obtener datos del usuario desde Firebase
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

        // Cierre de sesión
        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}