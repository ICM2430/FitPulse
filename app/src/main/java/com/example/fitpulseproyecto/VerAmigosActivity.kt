package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityVerAmigosBinding

class VerAmigosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerAmigosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerAmigosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.atras.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.agregar.setOnClickListener {
            val intent = Intent(this, AgregarAmigosActivity::class.java)
            startActivity(intent)
        }

        binding.verMas.setOnClickListener {
            val intent = Intent(this, VerUnAmigoActivity::class.java)
            startActivity(intent)
        }
    }
}