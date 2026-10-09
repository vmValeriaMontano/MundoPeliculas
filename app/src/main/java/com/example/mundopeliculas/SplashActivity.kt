package com.example.mundopeliculas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        //llamamos a la variable de auth
        auth = Firebase.auth

        //Creamos un tempo de 2.5s
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Si hay usuario logueado, pantalla Principal
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Si no hay sesión, a Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish() // Cierra el Splash para no volver atrás
        }, 2500)

    }
}