package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityCompartirEntrenoBinding

class CompartirEntrenoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompartirEntrenoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCompartirEntrenoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.atras.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}