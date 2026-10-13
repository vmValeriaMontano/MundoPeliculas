package com.example.mundopeliculas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.mundopeliculas.ui.home.HomeFragment
import com.example.mundopeliculas.ui.search.SearchFragment
import com.example.mundopeliculas.ui.notifications.NotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottomNavigation)

        val targetFragment = intent.getStringExtra("TARGET_FRAGMENT")

        if (savedInstanceState == null) {
            when (targetFragment) {
                "SEARCH" -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, SearchFragment())
                    }
                    bottomNav.selectedItemId = R.id.nav_search
                }
                "NOTIFICATIONS" -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, NotificationsFragment())
                    }
                    // No cambiamos el item de bottomNav porque notificaciones no está ahí
                }
                else -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, HomeFragment())
                    }
                    bottomNav.selectedItemId = R.id.nav_home
                }
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
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, SearchFragment())
                    }
                    true
                }

                R.id.nav_favorites -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, com.example.mundopeliculas.ui.favorites.FavoritesFragment())
                    }
                    true
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

        // bottomNav.selectedItemId = R.id.nav_home // Eliminado para no sobreescribir la lógica superior
    }
}