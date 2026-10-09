package com.example.mundopeliculas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        // Marcar la opción "Perfil" como seleccionada en esta Activity
        bottomNav.selectedItemId = R.id.nav_profile

        // Configurar la navegación clásica con Intent entre Activities
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true // Ya estamos en Perfil
                else -> {
                    Toast.makeText(this, "En construcción", Toast.LENGTH_SHORT).show()
                    true
                }
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