package com.example.fitpulseproyecto

import android.os.Bundle
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
    }
}