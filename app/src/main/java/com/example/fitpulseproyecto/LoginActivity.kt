package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setup()
    }

    private fun setup() {
        title = "Iniciar Sesión"

        binding.ingresa.setOnClickListener {
            val email = binding.correo.text.toString()
            val password = binding.contrasena.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "")
                        } else {
                            showError(it.exception)
                        }
                    }
            } else {
                showAlert("Por favor, rellena ambos campos")
            }
        }

        // Navegar al registro
        binding.registrarme.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showError(exception: Exception?) {
        Log.e("FirebaseAuthError", exception?.message ?: "Error desconocido")
        val message = when (exception) {
            is FirebaseAuthException -> when (exception.errorCode) {
                "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta."
                "ERROR_USER_NOT_FOUND" -> "No existe ningún usuario con este correo."
                else -> "Error desconocido: ${exception.errorCode}"
            }
            else -> "Error inesperado."
        }
        showAlert(message)
    }

    // Aquí eliminamos el parámetro provider y solo enviamos el email
    private fun showHome(email: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }
}