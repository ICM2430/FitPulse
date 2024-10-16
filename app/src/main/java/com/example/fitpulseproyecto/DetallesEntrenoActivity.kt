package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fitpulseproyecto.databinding.ActivityDetallesEntrenoBinding

class DetallesEntrenoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetallesEntrenoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetallesEntrenoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // Recibir los datos enviados desde la otra actividad
        val titulo = intent.getStringExtra("titulo")
        val fecha = intent.getStringExtra("fecha")
        val descripcion = intent.getStringExtra("descripcion")

        // Obtener referencias a los TextViews y mostrar los datos
        val tituloTextView: TextView = findViewById(R.id.titulo_entrenamiento)
        val fechaTextView: TextView = findViewById(R.id.fecha_entrenamiento)
        val descripcionTextView: TextView = findViewById(R.id.descripcion_entrenamiento)

        tituloTextView.text = titulo
        fechaTextView.text = fecha
        descripcionTextView.text = descripcion
    }
}