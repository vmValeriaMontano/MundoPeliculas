package com.example.mundopeliculas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.mundopeliculas.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, HomeFragment())
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, HomeFragment())
                    }
                    true
                }

                R.id.nav_search -> {
                    Toast.makeText(
                        this,
                        "La pantalla de búsqueda la conectaremos después",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }

                R.id.nav_favorites -> {
                    Toast.makeText(
                        this,
                        "Favoritos está pendiente de conectar",
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                }

                R.id.nav_profile -> {
                    startActivity(
                        Intent(this, ProfileActivity::class.java)
                    )
                    false
                }

                else -> false
            }
        }

        bottomNav.selectedItemId = R.id.nav_home
    }
}