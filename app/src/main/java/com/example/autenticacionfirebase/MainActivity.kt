package com.example.autenticacionfirebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.autenticacionfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si el usuario ya ha iniciado sesión
        checkUserAuthentication()
        binding.bIniciarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserAuthentication() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Si el usuario está autenticado, ir a HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finaliza MainActivity para que no se pueda volver a ella
        }
        // Si el usuario no está autenticado, permanecer en MainActivity
    }
}
