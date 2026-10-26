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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_GOOGLE_SIGN_IN = 9002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        db = Firebase.firestore

        val etName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoogleRegister = findViewById<Button>(R.id.btnGoogleRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // Configurar Google Sign-In con el Client ID de Firebase
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("369206001228-r5ri42fdgce34p7jr1vnkkb6n49av9b6.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Volver a la pantalla de Login
        tvLoginLink.setOnClickListener {
            finish()
        }

        // 1. Registro tradicional con correo y contraseña
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val userMap = hashMapOf(
                            "fullName" to name,
                            "email" to email
                        )

                        if (userId != null) {
                            db.collection("users").document(userId).set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finishAffinity()
                                }
                        }
                    } else {
                        val errorMessage = getFriendlyErrorMessage(task.exception?.message)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 2. Botón de Registro con Google
        btnGoogleRegister.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!, account.displayName ?: "Usuario Google", account.email ?: "")
                }
            } catch (e: ApiException) {
                val errorMessage = getFriendlyErrorMessage(e.message)
                Toast.makeText(this, getString(R.string.error_google_prefix, errorMessage), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, name: String, email: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (!document.exists()) {
                                    val userMap = hashMapOf(
                                        "fullName" to name,
                                        "email" to email
                                    )
                                    db.collection("users").document(userId).set(userMap)
                                }
                                Toast.makeText(this, getString(R.string.google_login_success), Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finishAffinity()
                            }
                    }
                } else {
                    val errorMessage = getFriendlyErrorMessage(task.exception?.message)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Función para traducir los errores técnicos a mensajes amigables
    private fun getFriendlyErrorMessage(error: String?): String {
        if (error == null) return getString(R.string.error_unknown)
        return when {
            error.contains("network", ignoreCase = true) -> getString(R.string.error_network)
            error.contains("email address is already in use", ignoreCase = true) -> getString(R.string.error_email_already_in_use)
            error.contains("weak-password", ignoreCase = true) || error.contains("password", ignoreCase = true) -> getString(R.string.error_password_weak)
            error.contains("badly formatted", ignoreCase = true) -> getString(R.string.error_invalid_email)
            else -> getString(R.string.error_prefix, error)
        }
    }
}