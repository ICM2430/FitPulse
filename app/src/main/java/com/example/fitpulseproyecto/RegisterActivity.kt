package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fitpulseproyecto.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setup()


        binding.loginRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setup() {
        title = "Registro"

        Log.i("Register", "ENTRO AL SETUP")

        binding.register.setOnClickListener {

            Log.i("Register", "DETECTO EL CLICK")
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()

            Log.i("Register", "${email}, ${password}")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
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
                "ERROR_EMAIL_ALREADY_IN_USE" -> "El correo ya está en uso por otra cuenta."
                "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres."
                else -> "Error desconocido: ${exception.errorCode}"
            }
            else -> "Error inesperado."
        }
        showAlert(message)
    }

    // Eliminamos el parámetro provider y solo enviamos el email
    private fun showHome(email: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }
}