package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityVerUnAmigoBinding

class VerUnAmigoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerUnAmigoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityVerUnAmigoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.atras.setOnClickListener {
            val intent = Intent(this, VerAmigosActivity::class.java)
            startActivity(intent)
        }
    }
}