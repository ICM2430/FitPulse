package com.example.fitpulseproyecto

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
        enableEdgeToEdge()
        setContentView(binding.root)
    }
}