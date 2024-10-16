package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityAgregarAmigosBinding

class AgregarAmigosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarAmigosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAgregarAmigosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.atras.setOnClickListener {
            val intent = Intent(this, VerAmigosActivity::class.java)
            startActivity(intent)
        }
    }
}