package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityIniciarEntrenoBinding

class IniciarEntrenoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIniciarEntrenoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIniciarEntrenoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.atras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
        binding.empezar.setOnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("destino", binding.destino.text.toString())
            startActivity(intent)
        }
    }
}