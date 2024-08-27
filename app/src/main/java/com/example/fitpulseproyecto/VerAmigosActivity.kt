package com.example.fitpulseproyecto

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
    }
}