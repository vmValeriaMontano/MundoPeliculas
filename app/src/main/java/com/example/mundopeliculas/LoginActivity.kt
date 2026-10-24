package com.example.mundopeliculas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_GOOGLE_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogleLogin = findViewById<Button>(R.id.btnGoogleLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Configurar Google Sign-In con el Client ID de tu JSON de Firebase
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("369206001228-r5ri42fdgce34p7jr1vnkkb6n49av9b6.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 1. Inicio de sesión con correo y contraseña
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        val errorMessage = getFriendlyErrorMessage(task.exception?.message)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 2. Botón de Google
        btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        // 3. Navegar a la pantalla de Registro
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                val errorMessage = getFriendlyErrorMessage(e.message)
                Toast.makeText(this, "Error en Google: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "¡Inicio de sesión con Google exitoso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val errorMessage = getFriendlyErrorMessage(task.exception?.message)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Función para traducir los errores técnicos a mensajes amigables
    private fun getFriendlyErrorMessage(error: String?): String {
        if (error == null) return "Ocurrió un error desconocido."
        return when {
            error.contains("network", ignoreCase = true) -> "Sin conexión a internet. Verifica tu red."
            error.contains("no user record", ignoreCase = true) || error.contains("user-not-found", ignoreCase = true) -> "No existe una cuenta registrada con este correo."
            error.contains("password", ignoreCase = true) || error.contains("credential", ignoreCase = true) -> "Contraseña incorrecta o correo inválido."
            error.contains("email address is already in use", ignoreCase = true) -> "Este correo ya está registrado."
            error.contains("badly formatted", ignoreCase = true) -> "El formato del correo electrónico no es válido."
            else -> "Error: $error"
        }
    }
}