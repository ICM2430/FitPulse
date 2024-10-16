package com.example.fitpulseproyecto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.fitpulseproyecto.databinding.ActivityHomeBinding
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM , yyyy", Locale("es", "CO"))
        val currentDate = dateFormat.format(Date())

        binding.config.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        binding.fecha.text = currentDate

        binding.iniciarActividad.setOnClickListener {
            val intent = Intent(this, IniciarEntrenoActivity::class.java)
            startActivity(intent)
        }
        binding.miPerfil.setOnClickListener {
            val intent = Intent(this, MiPerfilActivity::class.java)
            startActivity(intent)
        }
        binding.verHistorial.setOnClickListener {
            val intent = Intent(this, VerHistorialActivity::class.java)
            startActivity(intent)
        }
        binding.verAmigos.setOnClickListener{
            val intent = Intent(this, VerAmigosActivity::class.java)
            startActivity(intent)
        }
        binding.compartirEntreno.setOnClickListener {
            val intent = Intent(this, CompartirEntrenoActivity::class.java)
            startActivity(intent)
        }
        binding.salir.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}