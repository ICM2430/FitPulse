package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkUserAuthentication()

        binding.bIniciarSesion.setOnClickListener {
            val intent = Intent(baseContext, LoginActivity::class.java)
            startActivity(intent)
        }

    }
    private fun checkUserAuthentication() {
        Log.i("Firebase", "No llego a la instancia")
        val user = FirebaseAuth.getInstance().currentUser
        Log.i("Firebase", "Si llego a la instancia")
        if (user != null) {
            // Si el usuario está autenticado, ir a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finaliza MainActivity para que no se pueda volver a ella
        }
        // Si el usuario no está autenticado, permanecer en MainActivity
    }
}