package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityVerHistorialBinding

class VerHistorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerHistorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerHistorialBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        binding.botonEstaciones.setOnClickListener {
            val intent = Intent(this, EstacionesActivity::class.java)
            startActivity(intent)
        }


        val botonEntrenamiento1: Button = findViewById(R.id.titulo_entrenamiento_1)
        botonEntrenamiento1.setOnClickListener {
            val intent = Intent(this, DetallesEntrenoActivity::class.java)
            intent.putExtra("titulo", "Domingo Solitario hasta Sopó")
            intent.putExtra("fecha", "15 de septiembre 2024 - 09:00 AM")
            intent.putExtra("descripcion", "Un entrenamiento de ciclismo largo en solitario hasta Sopó.")
            startActivity(intent)
        }
    }
}