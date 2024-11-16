package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityMenuBinding
import com.google.firebase.auth.FirebaseAuth

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
    }

    private fun setupButtons() {

        binding.iniciarActividad.setOnClickListener {
            startActivity(Intent(baseContext, IniciarEntrenoActivity::class.java))
        }

        binding.miPerfil.setOnClickListener {
            startActivity(Intent(baseContext, MiPerfilActivity::class.java))
        }

        binding.verAmigos.setOnClickListener {
            startActivity(Intent(baseContext, AmigosActivity::class.java))
        }

        binding.config.setOnClickListener {
            startActivity(Intent(baseContext, ConfigurationActivity::class.java))
        }

        binding.verHistorial.setOnClickListener {
            startActivity(Intent(baseContext, VerHistorialActivity::class.java))
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(baseContext, HomeActivity::class.java))
        }


        binding.salir.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Cerrar sesión en Firebase
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}